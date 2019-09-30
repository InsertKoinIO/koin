package org.koin.dsl

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.qualifier.named
import org.koin.core.registry.ScopeRegistry

class BeanLifecycleTest {

    @After
    fun after() {
        stopKoin()
    }

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

    class Implementation

    @Test
    fun `closing a scope A does not affect bean definitions for other scope B when both scopes have same qualifiers`() {
        val koin = startKoin {
            modules(module {
                scope(named("A")) {
                    scoped { Implementation() }
                }
            })
        }.koin

        val scopeA = koin.createScope("123", named("A"))
        val scopeB = koin.createScope("234", named("A"))


        val scopeAValue = scopeA.get<Implementation>()
        val scopeBValue = scopeB.get<Implementation>()
        assertNotNull(scopeAValue)
        assertNotNull(scopeBValue)
        assertNotEquals(scopeAValue, scopeBValue)

        scopeA.close()

        assertEquals(scopeBValue, scopeB.get<Implementation>())
        try {
            scopeA.get<Implementation>()
            fail("Should not be able to resolve the instance from scope A because scope A is closed.")
        } catch(e: NoBeanDefFoundException) {

        }
    }

    @Test
    fun `closing koin releases related instances from scopes`() {
        val koin = startKoin {
            modules(module {
                scope(named("A")) {
                    scoped { Implementation() }
                }
            })
        }.koin

        val scopeA = koin.createScope("123", named("A"))
        val scopeB = koin.createScope("234", named("A"))

        assertNotNull(scopeA.get<Implementation>())
        assertNotNull(scopeB.get<Implementation>())

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
    fun `does not lose old scope instances when new scope instance is created`() {
        val koin = startKoin {
            modules(module {
                scope(named("A")) {
                    scoped { Implementation() }
                }
            })
        }.koin

        val scopeA = koin.createScope("123", named("A"))
        val scopeAInstance = scopeA.get<Implementation>()
        koin.createScope("234", named("A"))

        assertEquals(scopeAInstance, scopeA.get<Implementation>())
    }

}