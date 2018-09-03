package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.BeanOverrideException
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest


class GenericLists : AutoCloseKoinTest() {

    class Type1
    class Type2
    class Type3(val list: List<Type1>)

    @Test
    fun `list resolution failure`() {

        val type1Element = Type1()

        try {
            startKoin(
                listOf(
                    module {
                        single { listOf(type1Element) }
                    },
                    module {
                        single { listOf(Type2()) }
                    },
                    module {
                        single {
                            Type3(get())
                        }
                    }
                )
            )
            fail("should not start")
        } catch (e: BeanOverrideException) {
            assertNotNull(e)
        }
    }

    @Test
    fun `list resolution success - workaround`() {

        val type1Element = Type1()

        startKoin(
            listOf(
                module {
                    single("default") { listOf(type1Element) }
                },
                module {
                    single { listOf(Type2()) }
                },
                module {
                    single {
                        Type3(get("default"))
                    }
                }
            )
        )

        assertEquals(get<Type3>().list, listOf(type1Element))
    }
}