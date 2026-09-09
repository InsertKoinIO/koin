package org.koin.core.registry

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * KTZ-4829: InstanceRegistry.getAll was rewritten from filter + distinct + mapNotNull into a
 * single pass. Its contract is pinned here against an independent oracle built from the public
 * registry view: same candidates, same identity-based dedup, same order as `instances.values`.
 */
@OptIn(KoinInternalApi::class)
class GetAllSemanticsTest {

    interface Marker
    class ImplA : Marker
    class ImplB : Marker
    class ScopedImpl : Marker
    class Unrelated

    private fun buildKoin() = koinApplication {
        modules(
            module {
                // under two index keys (primary + secondary) -> must appear once
                single { ImplA() } bind Marker::class
                single { ImplB() } binds arrayOf(Marker::class)
                // primary type is the marker itself
                single<Marker>(named("direct")) { ImplA() }
                // neither primary nor secondary type matches
                single { Unrelated() }
                // matches the type but lives in another scope -> excluded from root getAll
                scope(named("other")) {
                    scoped { ScopedImpl() } bind Marker::class
                }
            },
        )
    }.koin

    @Test
    fun getAll_returns_each_matching_factory_once() {
        val koin = buildKoin()

        val result = koin.getAll<Marker>()

        assertEquals(3, result.size, "expected ImplA, ImplB and the direct Marker, each once")
        assertEquals(3, result.map { it::class to it }.distinctBy { it.second }.size, "duplicates returned")
        assertTrue(result.none { it is ScopedImpl }, "a scoped definition leaked into root getAll")
    }

    @Test
    fun getAll_matches_the_registry_iteration_order() {
        val koin = buildKoin()
        val rootQualifier = koin.scopeRegistry.rootScope.scopeQualifier

        val result = koin.getAll<Marker>()

        // Oracle: the pre-fusion pipeline, expressed over the public registry view.
        val expected = koin.instanceRegistry.instances.values
            .filter { it.beanDefinition.scopeQualifier == rootQualifier }
            .filter {
                it.beanDefinition.primaryType == Marker::class ||
                    it.beanDefinition.secondaryTypes.contains(Marker::class)
            }
            .distinct()
            .map { koin.get<Any>(it.beanDefinition.primaryType, it.beanDefinition.qualifier) }

        assertEquals(expected.size, result.size)
        expected.zip(result).forEach { (expectedInstance, actualInstance) ->
            assertSame(expectedInstance, actualInstance, "getAll order diverged from instances.values order")
        }
    }

    @Test
    fun scope_getAll_includes_own_scoped_definitions_and_root_ones() {
        val koin = buildKoin()
        val scope = koin.createScope("id", named("other"))

        val result = scope.getAll<Marker>()

        assertEquals(1, result.count { it is ScopedImpl })
        assertEquals(4, result.size, "expected the scoped definition plus the three root ones")
    }
}
