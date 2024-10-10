package org.koin.dsl

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

    @OptIn(KoinInternalApi::class)
    @Test
    fun test_onClose_order() {
        val closeLog = mutableListOf<String>()

        val module = module {
            single { Simple.ComponentC(get()) } onClose {
                if (it != null) {
                    closeLog += "c"
                }
            }
            single { Simple.ComponentA() } onClose {
                if (it != null) {
                    closeLog += "a"
                }
            }
            single { Simple.ComponentB(get()) } onClose {
                if (it != null) {
                    closeLog += "b"
                }
            }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module,
            )
        }.koin

        assertNotNull(koin.getOrNull<Simple.ComponentC>())
        assertEquals(
            expected = emptyList(), 
            actual = closeLog,
        )

        // check instances sorting meets instantiation order
        assertEquals(
            expected = listOf(Simple.ComponentA::class, Simple.ComponentB::class, Simple.ComponentC::class),
            actual = koin.instanceRegistry.instances.values.sorted().map { it.beanDefinition.primaryType },
        )

        koin.close()
        assertEquals(
            expected = listOf("c", "b", "a"), 
            actual = closeLog,
        )
    }

    @Test
    fun test_onClose_order_unload() {
        val closeLog = mutableListOf<String>()

        val moduleFirst = module {
            single { Simple.MySingle(42) } onClose {
                if (it != null) {
                    closeLog += "single"
                }
            }
        }

        val moduleSecond = module {
            single { Simple.ComponentC(get()) } onClose {
                if (it != null) {
                    closeLog += "c"
                }
            }
            single { Simple.ComponentA() } onClose {
                if (it != null) {
                    closeLog += "a"
                }
            }
            single { Simple.ComponentB(get()) } onClose {
                if (it != null) {
                    closeLog += "b"
                }
            }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                moduleFirst,
                moduleSecond,
            )
        }.koin

        assertNotNull(koin.getOrNull<Simple.MySingle>())
        assertNotNull(koin.getOrNull<Simple.ComponentC>())
        assertEquals(emptyList(), closeLog)

        koin.unloadModules(listOf(moduleSecond))
        assertEquals(
            expected = listOf("c", "b", "a"), 
            actual = closeLog,
        )

        koin.loadModules(listOf(moduleSecond))
        assertNotNull(koin.getOrNull<Simple.ComponentC>())

        koin.unloadModules(listOf(moduleSecond))
        assertEquals(
            expected = listOf("c", "b", "a", "c", "b", "a"),
            actual = closeLog,
        )

        koin.close()
        assertEquals(
            expected = listOf("c", "b", "a", "c", "b", "a", "single"),
            actual = closeLog,
        )
    }


    @Test
    fun test_onClose_order_scoped() {
        val closeLog = mutableListOf<String>()

        val scopeId = "_test_scope_id_"
        val scopeKey = named("_test_scope_")

        val module = module {
            scope(scopeKey) {
                scoped { Simple.ComponentC(get()) } onClose {
                    if (it != null) {
                        closeLog += "c"
                    }
                }
                scoped { Simple.ComponentA() } onClose {
                    if (it != null) {
                        closeLog += "a"
                    }
                }
                scoped { Simple.ComponentB(get()) } onClose {
                    if (it != null) {
                        closeLog += "b"
                    }
                }
            }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module,
            )
        }.koin

        assertEquals(
            expected = emptyList(),
            actual = closeLog,
        )

        koin.createScope(scopeId, scopeKey)
        assertEquals(
            expected = emptyList(),
            actual = closeLog,
        )

        koin.getScope(scopeId).let {
            assertNotNull(it.getOrNull<Simple.ComponentC>())
            it.close()
        }
        assertEquals(
            expected = listOf("c", "b", "a"),
            actual = closeLog,
        )

        koin.createScope(scopeId, scopeKey)
        assertNotNull(koin.getScope(scopeId).getOrNull<Simple.ComponentC>())
        assertEquals(
            expected = listOf("c", "b", "a"),
            actual = closeLog,
        )

        koin.close()
        assertEquals(
            expected = listOf("c", "b", "a", "c", "b", "a"),
            actual = closeLog,
        )
    }
}
