package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CloseDefinitionTest : KoinCoreTest() {

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

    class MyClass(val name: String)

    @Test
    fun override_onclose() {

        var cleanup = ""

        val overrideModule = module {
            single { MyClass("override") } onClose { cleanup = it?.name ?: "override" }
        }

        val module = module {
            // eager initialization
            single(createdAtStart = true) { MyClass("original") } onClose { cleanup = it?.name ?: "original" }
        }

        // override
        val koin = koinApplication { modules(module, overrideModule) }.koin

        val instance = koin.get<MyClass>()
        println("Accessing '${instance.name}'")
        assertEquals(instance.name, "override")

        koin.close()
        println("Koin stopped")
        assertEquals(cleanup, "override")
    }

    @Test
    fun override_onclose_2() {

        var cleanup = ""

        val overrideModule = module {
            single { MyClass("override") } onClose { cleanup = it?.name ?: "override" }
        }

        val module = module {
            // eager initialization
            single(createdAtStart = true) { MyClass("original") } onClose { cleanup = it?.name ?: "original" }
        }

        // override
        startKoin { modules(module, overrideModule) }

        val instance = KoinPlatform.getKoin().get<MyClass>()
        println("Accessing '${instance.name}'")
        assertEquals(instance.name, "override")

        stopKoin()
        println("Koin stopped")
        assertEquals(cleanup, "override")
    }
}
