package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getBeanDefinition

class KoinApplicationIsolationTest {

    @Test
    fun `can isolate several koin apps`() {
        val app1 = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }

        val app2 = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }

        val a1: Simple.ComponentA = app1.koin.get()
        val a2: Simple.ComponentA = app2.koin.get()

        assertNotEquals(a1, a2)
    }

    @Test
    fun `koin app instance run instance `() {
        val app = koinApplication {
            modules(
                    module {
                        single(createdAtStart = true) { Simple.ComponentA() }
                    })
        }
        app.createEagerInstances()

        app.getBeanDefinition(Simple.ComponentA::class)!!
        assertTrue(app.koin._scopeRegistry._rootScope!!._instanceRegistry.instances.values
                .first { instanceFactory -> instanceFactory.beanDefinition.primaryType == Simple.ComponentA::class }.isCreated()
        )
    }

    @Test
    fun `can isolate koin apps & standalone`() {
        startKoin {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }

        val app2 = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }

        val a1: Simple.ComponentA = KoinContextHandler.get().get()
        val a2: Simple.ComponentA = app2.koin.get()

        assertNotEquals(a1, a2)
        stopKoin()
    }

    @Test
    fun `stopping koin releases resources`() {
        val module = module {
            single { Simple.ComponentA() }
            scope(named<Simple>()) {
                scoped { Simple.ComponentB(get()) }
            }
        }
        startKoin {
            modules(module)
        }
        val a1: Simple.ComponentA = KoinContextHandler.get().get()
        val scope1 = KoinContextHandler.get().createScope("simple", named<Simple>())
        val b1: Simple.ComponentB = scope1.get()

        stopKoin()

        startKoin {
            modules(module)
        }
        val a2: Simple.ComponentA = KoinContextHandler.get().get()
        val scope2 = KoinContextHandler.get().createScope("simple", named<Simple>())
        val b2: Simple.ComponentB = scope2.get()

        assertNotEquals(a1, a2)
        assertNotEquals(b1, b2)

        stopKoin()
    }

    @Test
    fun `create multiple context without named qualifier`() {
        val koinA = koinApplication {
            modules(listOf(module {
                single { ModelA() }
            }, module {
                single { ModelB(get()) }
            }))
        }

        val koinB = koinApplication {
            modules(module {
                single { ModelC() }
            })
        }

        koinA.koin.get<ModelA>()
        koinA.koin.get<ModelB>()
        koinB.koin.get<ModelC>()

        try {
            koinB.koin.get<ModelA>()
            fail()
        } catch (e: Exception) {
        }
        try {
            koinB.koin.get<ModelB>()
            fail()
        } catch (e: Exception) {
        }
        try {
            koinA.koin.get<ModelC>()
            fail()
        } catch (e: Exception) {
        }
    }

    class ModelA
    class ModelB(val a: ModelA)
    class ModelC
}