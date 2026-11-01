package org.koin.core

import org.koin.core.parameter.parameterArrayOf
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * An explicit `null` passed for a nullable injected parameter must be consumed at its position, instead of making the
 * lookup fall back to the first (already consumed) value of the same type.
 *
 * The Koin Compiler Plugin (and the KSP processor) generate `params.get<T>()` / `params.getOrNull<T>()` for each
 * `@InjectedParam`, so `parametersOf("a", null, null)` used to inject `"a"` into all three `String?` parameters.
 *
 * @author - Alex Lillo
 */
class InjectedNullParametersTest {

    private class Three(val first: String, val second: String?, val third: String?)

    private class Mixed(val first: String, val second: String?, val third: String)

    private class Dependency

    private class WithDependency(val first: String, val dependency: Dependency, val second: String?, val third: String?)

    @Test
    fun nullableParamsAfterValueAreNull() {
        val params = parametersOf("a", null, null)

        assertEquals("a", params.get<String>())
        assertNull(params.getOrNull<String>())
        assertNull(params.getOrNull<String>())
    }

    @Test
    fun valueAfterNullIsKeptInPosition() {
        val params = parametersOf("a", null, "c")

        assertEquals("a", params.get<String>())
        assertNull(params.getOrNull<String>())
        assertEquals("c", params.get<String>())
    }

    @Test
    fun nullableParamsAfterValueAreNullWithArray() {
        val params = parameterArrayOf("a", null, "c")

        assertEquals("a", params.get<String>())
        assertNull(params.getOrNull<String>())
        assertEquals("c", params.get<String>())
    }

    @Test
    fun setParamsStillResolveByType() {
        val params = parameterSetOf(null, "a")

        assertEquals("a", params.getOrNull<String>())
    }

    @Test
    fun outOfOrderParamsStillResolveByType() {
        val params = parametersOf(42, "a")

        assertEquals("a", params.get<String>())
        assertEquals(42, params.get<Int>())
    }

    @Test
    fun typeLookupDoesNotConsumeNullPosition() {
        val params = parametersOf("a", null, "c")
        params.get<String>()

        // What the resolver does for a regular dependency resolved between two injected parameters
        assertNull(params.getOrNull<Dependency>(Dependency::class))

        assertEquals(1, params.index)
        assertNull(params.getOrNull<String>())
        assertEquals("c", params.get<String>())
    }

    @Test
    fun resolvesNullableParamsOfSameType() {
        val koin = koinApplication {
            modules(
                module {
                    factory { p -> Three(p.get(), p.getOrNull(), p.getOrNull()) }
                    factory { p -> Mixed(p.get(), p.getOrNull(), p.get()) }
                },
            )
        }.koin

        val three = koin.get<Three> { parametersOf("a", null, null) }
        assertEquals("a", three.first)
        assertNull(three.second)
        assertNull(three.third)

        val mixed = koin.get<Mixed> { parametersOf("a", null, "c") }
        assertEquals("a", mixed.first)
        assertNull(mixed.second)
        assertEquals("c", mixed.third)
    }

    @Test
    fun resolvesNullableParamsAroundRegularDependency() {
        val koin = koinApplication {
            modules(
                module {
                    single { Dependency() }
                    factory { p -> WithDependency(p.get(), get(), p.getOrNull(), p.getOrNull()) }
                },
            )
        }.koin

        val instance = koin.get<WithDependency> { parametersOf("a", null, "c") }

        assertEquals("a", instance.first)
        assertNull(instance.second)
        assertEquals("c", instance.third)
    }
}
