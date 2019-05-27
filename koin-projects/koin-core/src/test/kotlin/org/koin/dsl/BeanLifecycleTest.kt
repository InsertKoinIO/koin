package org.koin.dsl

import org.junit.Assert.assertEquals
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
}