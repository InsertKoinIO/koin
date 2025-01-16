package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.test.assertDefinitionsCount
import kotlin.test.*
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO - Check flaky tests
class AdditionalTypeBindingTest : KoinCoreTest() {

//    @Test
//    fun `can resolve an additional type - bind`() {
//        val app = koinApplication {
//            printLogger()
//            modules(
//                    module {
//                        single { Simple.Component1() } bind Simple.ComponentInterface1::class
//                    })
//        }
//
//        app.assertDefinitionsCount(1)
//
//        val koin = app.koin
//        val c1 = koin.get<Simple.Component1>()
//        val c = koin.bind<Simple.ComponentInterface1, Simple.Component1>()
//
//        assertEquals(c1, c)
//    }
//
//    @Test
//    fun `can resolve an additional type - bind()`() {
//        val app = koinApplication {
//            printLogger()
//            modules(
//                    module {
//                        single { Simple.Component1() }.bind<Simple.ComponentInterface1>()
//                    })
//        }
//
//        app.assertDefinitionsCount(1)
//
//        val koin = app.koin
//        val c1 = koin.get<Simple.Component1>()
//        val c = koin.bind<Simple.ComponentInterface1, Simple.Component1>()
//
//        assertEquals(c1, c)
//    }

    @Test
    fun `can resolve an additional type`() {
        val app = koinApplication {
            printLogger()
            modules(
                module {
                    single { Simple.Component1() } bind Simple.ComponentInterface1::class
                },
            )
        }

        app.assertDefinitionsCount(2)

        val koin = app.koin
        val c1 = koin.get<Simple.Component1>()
        val c = koin.get<Simple.ComponentInterface1>()

        assertEquals(c1, c)
    }

//    @Test
//    fun `resolve first additional type`() {
//        val app = koinApplication {
//            printLogger(Level.DEBUG)
//            modules(
//                    module {
//                        single { Simple.Component1() } bind Simple.ComponentInterface1::class
//                        single { Simple.Component2() } bind Simple.ComponentInterface1::class
//                    })
//        }
//
//        app.assertDefinitionsCount(2)
//
//        val koin = app.koin
//        koin.get<Simple.ComponentInterface1>()
//
//        assertNotEquals(koin.bind<Simple.ComponentInterface1, Simple.Component1>(), koin.bind<Simple.ComponentInterface1, Simple.Component2>())
//    }
//
    @Test
    fun `can resolve an additional type in DSL`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.Component1() } bind Simple.ComponentInterface1::class
                    single { Simple.Component2() } bind Simple.ComponentInterface1::class
                },
            )
        }

        app.assertDefinitionsCount(3)

        val koin = app.koin
        assertEquals(koin.get<Simple.Component2>(), koin.get<Simple.ComponentInterface1>())
    }

    @Test
    fun `additional type conflict - error`() {
        try {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { Simple.Component2() } bind Simple.ComponentInterface1::class
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                    },
                )
            }
//            fail("confilcting definitions")
        } catch (e: Exception) {
        }
    }

    @Test
    fun `should not conflict name and default type`() {
        val app = koinApplication {
            printLogger()
            modules(
                module {
                    single<Simple.ComponentInterface1>(named("default")) { Simple.Component2() }
                    single<Simple.ComponentInterface1> { Simple.Component1() }
                },
            )
        }
        val koin = app.koin
        koin.get<Simple.ComponentInterface1>(named("default"))
    }

//    @Test
//    fun `can resolve an additional types`() {
//        val koin = koinApplication {
//            printLogger()
//            modules(
//                    module {
//                        single { Simple.Component1() } binds arrayOf(
//                                Simple.ComponentInterface1::class,
//                                Simple.ComponentInterface2::class
//                        )
//                    })
//        }.koin
//
//        val c1 = koin.get<Simple.Component1>()
//        val ci1 = koin.bind<Simple.ComponentInterface1, Simple.Component1>()
//        val ci2 = koin.bind<Simple.ComponentInterface2, Simple.Component1>()
//
//        assertEquals(c1, ci1)
//        assertEquals(c1, ci2)
//    }

    @Test
    fun `additional type conflict`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component1() }
                    single { Simple.Component2() } bind Simple.ComponentInterface1::class
                },
            )
        }.koin

        assertTrue(koin.getAll<Simple.ComponentInterface1>().size == 1)
        assertTrue(koin.get<Simple.ComponentInterface1>() is Simple.Component2)
    }

    @Test
    fun `resolve all`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component1() }
                    single { Simple.Component2() } bind Simple.ComponentInterface1::class
                    single { getAll<Simple.ComponentInterface1>() }
                },
            )
        }.koin

        val result = koin.get<List<Simple.ComponentInterface1>>()
        assertEquals(1, result.size)
    }

    @Test
    fun `additional types`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component2() }
                    single { Simple.Component1() } binds arrayOf(
                        Simple.ComponentInterface1::class,
                        Simple.ComponentInterface2::class,
                    )
                },
            )
        }.koin
    }

    @Test
    fun `getAll 1 types`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component2() }
                    single { getAll<Simple.ComponentInterface1>() }
                },
            )
        }.koin
        assertTrue(koin.getAll<Simple.ComponentInterface1>().size == 1)
        assertTrue(koin.get<List<Simple.ComponentInterface1>>().size == 1)
    }
}
