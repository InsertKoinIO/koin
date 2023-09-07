package org.koin.dsl

import org.koin.Simple
import org.koin.core.logger.Level
import kotlin.test.Test
import kotlin.test.assertTrue

class CloseDefinitionTest {

    @Test
    fun test_onClose() {
        var closed = false

        val koin = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() } onClose {
                        closed = true
                    }
                },
            )
        }.koin

        koin.close()
        assertTrue(closed)
    }

    @Test
    fun test_onClose_from_unload() {
        var closed = false

        val module = module {
            single { Simple.ComponentA() } onClose {
                closed = !closed
                println("closing ComponentA - closed = $closed")
            }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module,
            )
        }.koin

        assertTrue(!closed)
        koin.unloadModules(listOf(module))
        assertTrue(closed)
        koin.loadModules(listOf(module))
        koin.unloadModules(listOf(module))
        assertTrue(!closed)
    }
}
