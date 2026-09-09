package org.koin.core.registry

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Kind
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * KTZ-4829: resolveDefinition keeps a lazily populated, allocation-free cache in front of the
 * String-keyed map. Every write to the registry must drop the affected entries, a repeat lookup
 * must agree with the String map, and a caller that writes a hand-built String key must see
 * exactly the behavior it had before.
 */
@OptIn(KoinInternalApi::class)
class TypedIndexTest {

    interface Marker
    class Impl : Marker

    @Test
    fun override_is_visible_through_the_fast_path() {
        val koin = koinApplication {
            modules(
                module { single { Simple.ComponentA() } },
                module { single { Simple.ComponentA() } },
            )
        }.koin
        val root = koin.scopeRegistry.rootScope.scopeQualifier

        // one key, one factory left in the String map: the second module's
        val winner = koin.instanceRegistry.instances.values.single { it.beanDefinition.primaryType == Simple.ComponentA::class }

        assertSame(winner, koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, root), "fast path returned an overridden factory")
        koin.get<Simple.ComponentA>()
        assertEquals(true, winner.isCreated(), "resolution did not go through the winning factory")
    }

    @Test
    fun unloaded_definitions_are_gone_from_the_fast_path() {
        val moduleA = module { single { Simple.ComponentA() } bind Any::class }
        val koin = koinApplication { modules(moduleA) }.koin
        assertNotNull(koin.getOrNull<Simple.ComponentA>())
        assertNotNull(koin.getOrNull<Any>())

        koin.unloadModules(listOf(moduleA))

        assertNull(koin.getOrNull<Simple.ComponentA>(), "primary type still resolvable after unload")
        assertNull(koin.getOrNull<Any>(), "secondary type still resolvable after unload")
    }

    @Test
    fun reload_after_unload_resolves_the_new_factory() {
        val moduleA = module { single { Simple.ComponentA() } }
        val koin = koinApplication { modules(moduleA) }.koin
        val first = koin.get<Simple.ComponentA>()

        koin.unloadModules(listOf(moduleA))
        koin.loadModules(listOf(module { single { Simple.ComponentA() } }))

        assertNotSame(first, koin.get<Simple.ComponentA>(), "a stale fast-path entry survived unload")
    }

    @Test
    fun close_clears_the_fast_path() {
        val koin = koinApplication { modules(module { single { Simple.ComponentA() } }) }.koin
        koin.get<Simple.ComponentA>()

        koin.close()

        assertEquals(0, koin.instanceRegistry.size())
        assertNull(koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, koin.scopeRegistry.rootScope.scopeQualifier))
    }

    @Test
    fun qualified_and_scoped_definitions_resolve_through_the_fast_path() {
        val koin = koinApplication {
            modules(
                module {
                    single(named("a")) { Impl() } bind Marker::class
                    single(named("b")) { Impl() } bind Marker::class
                    scope(named("s")) { scoped { Impl() } bind Marker::class }
                },
            )
        }.koin

        assertNotSame(koin.get<Marker>(named("a")), koin.get<Marker>(named("b")))
        assertSame(koin.get<Impl>(named("a")), koin.get<Marker>(named("a")))
        assertNull(koin.getOrNull<Marker>(), "an unqualified lookup must not match a qualified definition")

        val scope = koin.createScope("id", named("s"))
        assertNotNull(scope.getOrNull<Marker>())
        assertSame(scope.get<Impl>(), scope.get<Marker>())
    }

    // A caller writing a String key that is not indexKey() of the factory's own types is not
    // mirrored into the fast path. Its behavior must be exactly what it was: resolvable through
    // the String map by that key, invisible to type-based lookups.
    @Test
    fun hand_built_key_keeps_string_map_behavior() {
        val koin = koinApplication { }.koin
        val root = koin.scopeRegistry.rootScope.scopeQualifier
        val definition = BeanDefinition(root, Simple.ComponentA::class, null, { Simple.ComponentA() }, Kind.Singleton)
        val factory = SingleInstanceFactory(definition)

        koin.instanceRegistry.saveMapping(allowOverride = true, mapping = "custom-key", factory = factory)

        assertSame(factory, koin.instanceRegistry.instances["custom-key"])
        assertNull(koin.getOrNull<Simple.ComponentA>(), "a hand-built key became resolvable by type")
        assertNull(koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, root))
    }

    @Test
    fun fast_path_and_string_path_agree_on_every_registered_key() {
        val koin = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                    single(named("q")) { Simple.ComponentA() }
                    single { Impl() } bind Marker::class
                    scope(named("s")) { scoped { Simple.ComponentB(get()) } }
                },
            )
        }.koin

        koin.instanceRegistry.instances.forEach { (key, factory) ->
            val definition = factory.beanDefinition
            val types = listOf(definition.primaryType) + definition.secondaryTypes
            val viaFastPath = types.mapNotNull { type ->
                koin.instanceRegistry.resolveDefinition(type, definition.qualifier, definition.scopeQualifier)
            }
            assertEquals(types.size, viaFastPath.size, "key '$key' not resolvable for every type of its definition")
            viaFastPath.forEach { resolved -> assertSame(koin.instanceRegistry.instances[key], resolved, "key '$key' resolved a different factory") }
        }
    }

    // The cache is filled by the first lookup; an override written afterwards must evict it.
    @Test
    fun override_after_a_cached_lookup_is_visible() {
        val koin = koinApplication { modules(module { single { Simple.ComponentA() } }) }.koin
        val root = koin.scopeRegistry.rootScope.scopeQualifier
        val first = koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, root)
        assertSame(first, koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, root), "repeat lookup diverged")

        koin.loadModules(listOf(module { single { Simple.ComponentA() } }), allowOverride = true)

        val afterOverride = koin.instanceRegistry.resolveDefinition(Simple.ComponentA::class, null, root)
        assertNotSame(first, afterOverride, "a stale cached factory survived an override")
        assertSame(koin.instanceRegistry.instances.values.single(), afterOverride)
    }

    @Test
    fun unload_after_a_cached_lookup_evicts_it() {
        val moduleA = module { single { Simple.ComponentA() } }
        val koin = koinApplication { modules(moduleA) }.koin
        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentA>()

        koin.unloadModules(listOf(moduleA))

        assertNull(koin.getOrNull<Simple.ComponentA>(), "a cached factory survived unload")
    }
}
