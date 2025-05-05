package org.koin.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParametersInjectionTest {

    @Test
    fun `can create a single with parameters`() {
        val app = koinApplication {
            modules(
                module {
                    single { (i: Int) -> Simple.MySingle(i) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)
    }

    @Test
    fun inject_param_get_or_null() {
        ensureCanInjectParam(
            module {
                singleOf(Simple::MySingle)
                single { Simple.MySingleAndNull(getOrNull(), get()) }
            },
        )

        ensureCanInjectParam(
            module {
                singleOf(Simple::MySingle)
                single { (i: Int) -> Simple.MySingleAndNull(getOrNull(), get { parametersOf(i) }) }
            },
        )

        ensureCanInjectParam(
            module {
                singleOf(Simple::MySingle)
                single { (i: Int) -> Simple.MySingleAndNull(getOrNull { parametersOf(i) }, get { parametersOf(i) }) }
            },
        )
    }

    private fun ensureCanInjectParam(module1: Module) {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module1,
            )
        }

        val koin = app.koin
        val a: Simple.MySingleAndNull = koin.get { parametersOf(42) }

        assertEquals(42, a.ms.id, "id is the right injected")
//        assertTrue("parameter stack is empty") { koin.scopeRegistry.rootScope._parameterStackLocal.get()?.isEmpty() == true }
    }

    @Test
    fun `can create a single with parameters in order`() {
        val app = koinApplication {
            modules(
                module {
                    single { p -> Simple.MyTwinSingle(p[0], p[1]) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MyTwinSingle = koin.get { parametersOf(42, 24) }

        assertEquals(42, a.i1)
        assertEquals(24, a.i2)
    }

    @Test
    fun `can create a single with parameters in order - destruct`() {
        val app = koinApplication {
            modules(
                module {
                    single { (a: Int, b: Int) -> Simple.MyTwinSingle(a, b) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MyTwinSingle = koin.get { parametersOf(42, 24) }

        assertEquals(42, a.i1)
        assertEquals(24, a.i2)
    }

    @Test
    fun `can create a single with parameters in order - dsl ctor`() {
        val app = koinApplication {
            modules(
                module {
                    singleOf(Simple::MyTwinSingle)
                },
            )
        }

        val koin = app.koin
        val a: Simple.MyTwinSingle = koin.get { parametersOf(1, 2) }

        assertEquals(1, a.i1)
        assertEquals(2, a.i2)
    }

    @Test
    fun `allow parameters injection in ctor dsl`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                    singleOf(Simple::MyTwinSingleMix)
                },
            )
        }

        val koin = app.koin
        val a: Simple.MyTwinSingleMix = koin.get { parametersOf(1, 2) }

        assertEquals(1, a.i1)
        assertEquals(2, a.i2)
    }

    @Test
    fun nullable_injection_param() {
        val app = koinApplication {
            modules(
                module {
                    single { p -> Simple.MySingleWithNull(p.getOrNull()) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingleWithNull = koin.get()

        assertNull(a.id)
    }

    internal class MyOptionalSingle(val i: Int, val o: String? = null)

    @Test
    fun nullable_injection_param_in_graph() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { p -> MyOptionalSingle(p.get(), getOrNull()) }
                },
            )
        }

        val koin = app.koin
        val value = 42
        val a: MyOptionalSingle = koin.get { parametersOf(value) }

        assertEquals(value, a.i)
        assertNull(a.o)
    }

    @Test
    fun `can create a single with parameters - using param object resolution`() {
        val app = koinApplication {
            modules(
                module {
                    single { params -> Simple.MySingle(params.get()) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)
    }

    @Test
    fun `can create a single with parameters - using graph resolution`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.MySingle(get()) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)
    }

    @Test
    fun `can create a single with parameters - using double graph resolution`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.MySingle(get()) }
                    single(named("2")) { Simple.MySingle(get()) }
                },
            )
        }
        val koin = app.koin
        assertEquals(42, koin.get<Simple.MySingle> { parametersOf(42) }.id)
        assertEquals(24, koin.get<Simple.MySingle>(named("2")) { parametersOf(24) }.id)
    }

    @Test
    fun `can create a single with nullable parameters`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (i: Int?) -> Simple.MySingleWithNull(i) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingleWithNull = koin.get { parametersOf(null) }

        assertEquals(null, a.id)
    }

    @Test
    fun `can get a single created with parameters - no need of give it again`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (i: Int) -> Simple.MySingle(i) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)

        val a2: Simple.MySingle = koin.get()

        assertEquals(42, a2.id)
    }

    @Test
    fun `can create factories with params`() {
        val app = koinApplication {
            modules(
                module {
                    factory { (i: Int) -> Simple.MyIntFactory(i) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MyIntFactory = koin.get { parametersOf(42) }
        val a2: Simple.MyIntFactory = koin.get { parametersOf(43) }

        assertEquals(42, a.id)
        assertEquals(43, a2.id)
    }

    @Test
    fun `chained factory injection`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory { (i: Int) -> Simple.MyIntFactory(i) }
                    factory { (s: String) -> Simple.MyStringFactory(s) }
                    factory { (i: Int, s: String) ->
                        Simple.AllFactory(
                            get { parametersOf(i) },
                            get { parametersOf(s) },
                        )
                    }
                },
            )
        }.koin

        val f = koin.get<Simple.AllFactory> { parametersOf(42, "42") }

        assertEquals(42, f.ints.id)
        assertEquals("42", f.strings.s)
    }

    @Test
    fun `inject in graph`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.MySingle(it.get()) }
                },
            )
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)
    }

    @Test
    fun `chained factory injection - graph`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory { p -> Simple.MyIntFactory(p.get()) }
                    factory { Simple.MyStringFactory(it.get()) }
                    factory { (i: Int, s: String) ->
                        Simple.AllFactory(
                            get { parametersOf(i) },
                            get { parametersOf(s) },
                        )
                    }
                },
            )
        }.koin

        val f = koin.get<Simple.AllFactory> { parametersOf(42, "42") }

        assertEquals(42, f.ints.id)
        assertEquals("42", f.strings.s)
    }

    @Test
    fun `inject across multiple threads`(): TestResult {
        val times = 100

        return runTest {
            val app = koinApplication {
                modules(
                    module {
                        factory { (i: Int) -> Simple.MyIntFactory(i) }
                    },
                )
            }

            val koin = app.koin

            repeat(times) {
                val range = (0 until times)
                val deferreds = range.map {
                    async(Dispatchers.Default) {
                        koin.get<Simple.MyIntFactory> { parametersOf(it) }
                    }
                }
                val values = awaitAll(*deferreds.toTypedArray())
                assertEquals(range.map { it }, values.map { it.id })
            }
        }
    }
}
