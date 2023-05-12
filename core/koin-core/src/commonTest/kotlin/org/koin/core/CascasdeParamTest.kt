package org.koin.core

import org.koin.Simple
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class CascasdeParamTest {

    @Test
    fun can_cascade_param_full_ctor_dsl() {
        val koin = koinApplication {
            modules(
                module {
                    factoryOf(Simple::MyIntFactory)
                    factoryOf(Simple::MyStringFactory)
                    factoryOf(Simple::AllFactory)
                })
        }.koin

        val intParam = 42
        val stringParam = "_string_"
        val allFactory = koin.get<Simple.AllFactory> { parametersOf(intParam, stringParam) }
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
                })
        }.koin

        val intParam = 42
        val stringParam = "_string_"
        val allFactory = koin.get<Simple.AllFactory2> { parametersOf(stringParam, intParam) }
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
                            get())
                    }
                })
        }.koin

        val intParam = 43
        val stringParam = "_string2_"
        val allFactory = koin.get<Simple.AllFactory> { parametersOf(intParam, stringParam) }
        assertEquals(intParam, allFactory.ints.id)
        assertEquals(stringParam, allFactory.strings.s)
    }
}