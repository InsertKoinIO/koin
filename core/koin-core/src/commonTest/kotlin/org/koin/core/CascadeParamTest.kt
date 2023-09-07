package org.koin.core

import org.koin.Simple
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parameterArrayOf
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CascadeParamTest {

    @Test
    fun consume_bad_value() {
        val intParam: Int = 42
        val stringParam = "_string_"
        val p = parametersOf(intParam, stringParam)

        assertEquals(0, p.index)
        assertEquals(stringParam, p.getOrNull<String>())
        assertEquals(0, p.index)
        assertEquals(intParam, p.getOrNull<Int>())
        assertEquals(stringParam, p.getOrNull<String>())
    }

    @Test
    fun parameter_array() {
        val intParam: Int = 42
        val stringParam = "_string_"
        val p = parameterArrayOf(intParam, stringParam)

        assertEquals(0, p.index)
        assertNull(p.getOrNull<String>())
        assertEquals(intParam, p.get<Int>())
        assertEquals(stringParam, p.get<String>())
        assertEquals(1, p.index)
    }

    @Test
    fun parameter_set() {
        val intParam: Int = 42
        val stringParam = "_string_"
        val p = parameterSetOf(intParam, stringParam)

        assertEquals(0, p.index)
        assertEquals(stringParam, p.getOrNull<String>())
        assertEquals(intParam, p.getOrNull<Int>())
        assertEquals(stringParam, p.getOrNull<String>())
        assertEquals(0, p.index)
    }

    @Test
    fun can_cascade_param_full_ctor_dsl() {
        val koin = koinApplication {
            modules(
                module {
                    factoryOf(Simple::MyIntFactory)
                    factoryOf(Simple::MyStringFactory)
                    factoryOf(Simple::AllFactory)
                },
            )
        }.koin

        val intParam = 42
        val stringParam = "_string_"
        val allFactory = koin.get<Simple.AllFactory> { parameterArrayOf(intParam, stringParam) }
        assertEquals(intParam, allFactory.ints.id)
        assertEquals(stringParam, allFactory.strings.s)
    }

    @Test
    fun can_cascade_param_full_ctor_dsl2() {
        val koin = koinApplication {
            modules(
                module {
                    factoryOf(Simple::MyIntFactory)
                    factoryOf(Simple::MyStringFactory)
                    factoryOf(Simple::AllFactory2)
                },
            )
        }.koin

        val intParam = 42
        val stringParam = "_string_"
        val allFactory = koin.get<Simple.AllFactory2> { parameterArrayOf(stringParam, intParam) }
        assertEquals(intParam, allFactory.ints.id)
        assertEquals(stringParam, allFactory.strings.s)
    }

    @Test
    fun can_cascade_param_full_ctor_dsl1() {
        val koin = koinApplication {
            modules(
                module {
                    factoryOf(Simple::MyIntFactory)
                    factoryOf(Simple::MyStringFactory)
                    factory {
                        Simple.AllFactory(
                            get(),
                            get(),
                        )
                    }
                },
            )
        }.koin

        val intParam = 43
        val stringParam = "_string2_"
        val allFactory = koin.get<Simple.AllFactory> { parametersOf(intParam, stringParam) }
        assertEquals(intParam, allFactory.ints.id)
        assertEquals(stringParam, allFactory.strings.s)
    }
}
