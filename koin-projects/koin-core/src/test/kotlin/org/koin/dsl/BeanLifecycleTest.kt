package org.koin.dsl

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
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
     * https://github.com/InsertKoinIO/koin/issues/492
     */
    @Test
    fun `closing scope A1 does not affect bean definitions of scope A2 when both scope instances have the same scope name A`() {
        val koin = koinApplication {
            modules(module {
                scope(named("A")) {
                    scoped { Simple.Component1() }
                }
            })
        }.koin

        val scopeA1 = koin.createScope("123", named("A"))
        val scopeA2 = koin.createScope("234", named("A"))

        val scopeA1Value = scopeA1.get<Simple.Component1>()
        val scopeA2Value = scopeA2.get<Simple.Component1>()
        assertNotNull(scopeA1Value)
        assertNotNull(scopeA2Value)
        assertNotEquals(scopeA1Value, scopeA2Value)

        scopeA1.close()

        assertEquals(scopeA2Value, scopeA2.get<Simple.Component1>())
        try {
            scopeA1.get<Simple.Component1>()
            fail("Should not be able to resolve the instance from scope A because scope A is closed.")
        } catch(e: NoBeanDefFoundException) {

        }
    }

    @Test
    fun `closing koin releases related instances from scopes`() {
        val koin = koinApplication {
            modules(module {
                scope(named("A")) {
                    scoped { Simple.Component1() }
                }
            })
        }.koin

        val scopeA = koin.createScope("123", named("A"))
        val scopeB = koin.createScope("234", named("A"))

        assertNotNull(scopeA.get<Simple.Component1>())
        assertNotNull(scopeB.get<Simple.Component1>())

        val definitionsA = scopeA.beanRegistry.getAllDefinitions().toList()
        val definitionsB = scopeB.beanRegistry.getAllDefinitions().toList()
        assert(definitionsA.isNotEmpty())
        assert(definitionsB.isNotEmpty())

        koin.close()

        definitionsA.forEach { assertNull(it.instance) }
        definitionsB.forEach { assertNull(it.instance) }
        assertEquals(0, scopeA.beanRegistry.getAllDefinitions().size)
        assertEquals(0, scopeB.beanRegistry.getAllDefinitions().size)
    }

    @Test
    fun `closes bean definitions in root scope when koin is closed`() {
        lateinit var definition: BeanDefinition<Simple.Component1>
        val koin = koinApplication {
            modules(module {
                definition = single { Simple.Component1() }
            })
        }.koin

        koin.get<Simple.Component1>()
        assertNotNull(definition.instance)

        koin.close()
        assertNull(definition.instance)
    }
}