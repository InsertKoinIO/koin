package org.koin.core.definition

import org.koin.Simple
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Regression tests for the mutable-backing-list refactor of [BeanDefinition.secondaryTypes]
 * (4.2.2): in-place append via bind()/binds(), replace semantics of the setter, and
 * defensive copies against caller aliasing.
 */
class BeanDefinitionSecondaryTypesTest {

    private fun definition(secondaryTypes: List<KClass<*>> = emptyList()) = BeanDefinition(
        scopeQualifier = named("test-scope"),
        primaryType = Simple.ComponentA::class,
        definition = { Simple.ComponentA() },
        kind = Kind.Singleton,
        secondaryTypes = secondaryTypes,
    )

    @Test
    fun constructor_snapshots_incoming_list() {
        val source = mutableListOf<KClass<*>>(Simple.ComponentInterface1::class)
        val def = definition(source)

        source.add(Simple.ComponentInterface2::class)

        assertEquals(listOf(Simple.ComponentInterface1::class), def.secondaryTypes)
    }

    @Test
    fun setter_replaces_content() {
        val def = definition(listOf(Simple.ComponentInterface1::class))

        def.secondaryTypes = listOf(Simple.ComponentInterface2::class)

        assertEquals(listOf(Simple.ComponentInterface2::class), def.secondaryTypes)
    }

    @Test
    fun self_assignment_is_a_noop() {
        val def = definition(listOf(Simple.ComponentInterface1::class, Simple.ComponentInterface2::class))

        def.secondaryTypes = def.secondaryTypes

        assertEquals(
            listOf(Simple.ComponentInterface1::class, Simple.ComponentInterface2::class),
            def.secondaryTypes,
        )
    }

    @Test
    fun assigning_a_view_of_own_list_does_not_corrupt() {
        val def = definition(listOf(Simple.ComponentInterface1::class, Simple.ComponentInterface2::class))

        def.secondaryTypes = def.secondaryTypes.subList(0, 1)

        assertEquals(listOf(Simple.ComponentInterface1::class), def.secondaryTypes)
    }

    @Test
    fun assigning_a_derived_list_works() {
        val def = definition(listOf(Simple.ComponentInterface1::class, Simple.ComponentInterface2::class))

        def.secondaryTypes = def.secondaryTypes.filter { it == Simple.ComponentInterface2::class }

        assertEquals(listOf(Simple.ComponentInterface2::class), def.secondaryTypes)
    }

    @Test
    fun getter_is_stable_across_binds() {
        val def = definition()
        val first = def.secondaryTypes

        def.addSecondaryType(Simple.ComponentInterface1::class)

        // in-place append: same instance, new content visible
        assertSame(first, def.secondaryTypes)
        assertEquals(listOf(Simple.ComponentInterface1::class), def.secondaryTypes)
    }

    @Test
    fun dsl_bind_and_binds_append_and_resolve() {
        val koin = koinApplication {
            modules(
                module {
                    single { Simple.Component1() } bind Simple.ComponentInterface2::class
                    single { Simple.Component2() } binds arrayOf(Simple.ComponentInterface1::class)
                },
            )
        }.koin

        assertEquals(koin.get<Simple.Component1>(), koin.get<Simple.ComponentInterface2>())
        assertEquals(koin.get<Simple.Component2>(), koin.get<Simple.ComponentInterface1>())
    }
}
