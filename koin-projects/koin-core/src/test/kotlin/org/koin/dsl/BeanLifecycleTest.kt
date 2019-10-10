package org.koin.dsl

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named

class BeanLifecycleTest {

    @Test
    fun `declare onClose for single`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        single { Simple.Component1() } onClose { result = "closing" }
                    })
        }

        val koin = app.koin
        koin.close()
        assertEquals("closing", result)
    }

    @Test
    fun `declare onClose for factory`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        factory { Simple.Component1() } onClose { result = "closing" }
                    })
        }

        val koin = app.koin
        koin.close()
        assertEquals("closing", result)
    }

    @Test
    fun `declare onClose for scoped`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        scope(named("test")) {
                            scoped { Simple.Component1() } onClose { result = "closing" }
                        }
                    })
        }

        val koin = app.koin
        koin.createScope("id", named("test"))
        koin.close()
        assertEquals("closing", result)
    }

    @Test
    fun `declare onRelease for single`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        single { Simple.Component1() } onRelease { result = "release" }
                    })
        }

        val koin = app.koin
        koin.close()
        assertEquals("", result)
    }

    @Test
    fun `declare onRelease for factory`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        factory { Simple.Component1() } onRelease { result = "release" }
                    })
        }

        val koin = app.koin
        koin.close()
        assertEquals("", result)
    }

    @Test
    fun `declare onRelease for scoped`() {
        var result = ""
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        scope(named("test")) {
                            scoped { Simple.Component1() } onRelease { result = "release" }
                        }
                    })
        }

        val koin = app.koin
        val scopeInstance = koin.createScope("test", named("test"))
        scopeInstance.get<Simple.Component1>()
        scopeInstance.close()
        assertEquals("release", result)
    }

    /**
     * https://github.com/InsertKoinIO/koin/issues/566
     */
    @Test
    fun `scope A1 does not lose already created scoped instances when scope A2 is created`() {
        val koin = koinApplication {
            modules(module {
                scope(named("A")) {
                    scoped { Simple.Component1() }
                }
            })
        }.koin

        val scopeA1 = koin.createScope("123", named("A"))
        val scopeA1Instance = scopeA1.get<Simple.Component1>()
        val scopeA2 = koin.createScope("234", named("A"))

        assertEquals(scopeA1Instance, scopeA1.get<Simple.Component1>())
        assertNotEquals(scopeA1Instance, scopeA2.get<Simple.Component1>())
    }
}