package org.koin.core

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getDefinition

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

        val def = app.getDefinition(Simple.ComponentA::class)!!
        assertTrue(def.instance.isCreated())
    }

    @Test
    fun `can isolate koin apps & standaline`() {
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

        val a1: Simple.ComponentA = GlobalContext.get().koin.get()
        val a2: Simple.ComponentA = app2.koin.get()

        assertNotEquals(a1, a2)
        stopKoin()
    }
}