package org.koin.core

import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class KoinApplicationIsolationTest {

    @Test
    fun `can isolate several koin apps`() {
        val app1 = koinApplication {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val app2 = koinApplication {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val a1: Simple.ComponentA = app1.koin.get()
        val a2: Simple.ComponentA = app2.koin.get()

        assertNotEquals(a1, a2)
    }

    @Test
    fun `can isolate koin apps & standaline`() {
        koinApplication {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }.start()

        val app2 = koinApplication {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val a1: Simple.ComponentA = StandAloneKoinApplication.get().koin.get()
        val a2: Simple.ComponentA = app2.koin.get()

        assertNotEquals(a1, a2)
        StandAloneKoinApplication.get().stop()
    }

}