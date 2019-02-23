package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple

class BeanLifecycleTest {

    @Test
    fun `declare onClose for single`() {
        var result = ""
        val app = koinApplication {
            defaultLogger()
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
            defaultLogger()
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
            defaultLogger()
            modules(
                    module {
                        scope("test") {
                            scoped { Simple.Component1() } onClose { result = "closing" }
                        }
                    })
        }

        val koin = app.koin
        koin.close()
        assertEquals("closing", result)
    }

    @Test
    fun `declare onRelease for single`() {
        var result = ""
        val app = koinApplication {
            defaultLogger()
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
            defaultLogger()
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
            defaultLogger()
            modules(
                    module {
                        scope("test") {
                            scoped { Simple.Component1() } onRelease { result = "release" }
                        }
                    })
        }

        val koin = app.koin
        val scopeInstance = koin.createScope("test","test")
        scopeInstance.get<Simple.Component1>()
        scopeInstance.close()
        assertEquals("release", result)
    }
}