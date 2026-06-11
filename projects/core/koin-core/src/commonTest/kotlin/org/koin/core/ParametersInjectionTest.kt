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

    // Test classes for #2337
    class SharedViewModel(val id: Int)
    class ConsumerViewModel(val shared: SharedViewModel)

    /**
     * Test for issue #2337 - Injected parameters should take precedence over registry resolution
     * When passing an existing instance via parametersOf(), that instance should be used directly
     * instead of Koin trying to resolve/recreate it from the registry definition.
     *
     * This simulates the common Compose Navigation pattern where a SharedViewModel is passed
     * to another ViewModel via parametersOf().
     */
    @Test
    fun `injected parameter instance should be used directly - not resolved from registry`() {
        // Pre-existing SharedViewModel instance (simulates one already created in a parent screen)
        val existingSharedVM = SharedViewModel(42)

        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    // SharedViewModel has a definition that requires an Int parameter
                    // If Koin tries to resolve from registry, it will fail without the Int
                    single { (id: Int) -> SharedViewModel(id) }
                    // ConsumerViewModel depends on SharedViewModel
                    factory { ConsumerViewModel(get()) }
                },
            )
        }

        val koin = app.koin

        // Pass existing SharedViewModel instance via parameters
        // This should use the passed instance directly, NOT try to create a new one from registry
        val consumer: ConsumerViewModel = koin.get { parametersOf(existingSharedVM) }

        // Verify that the exact same instance was used (not a new one from registry)
        assertEquals(existingSharedVM, consumer.shared)
        assertEquals(42, consumer.shared.id)
    }

    /**
     * Additional test for #2337 - Ensure parameter instance takes priority over definition
     * even when definition exists and could technically be resolved
     */
    @Test
    fun `parameter instance takes priority over existing definition`() {
        val passedInstance = Simple.ComponentA()

        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    // ComponentA has a definition in registry
                    single { Simple.ComponentA() }
                    // ComponentB depends on ComponentA
                    factory { Simple.ComponentB(get()) }
                },
            )
        }

        val koin = app.koin

        // Pass our own ComponentA instance - should use this one, not the one from registry
        val b: Simple.ComponentB = koin.get { parametersOf(passedInstance) }

        // Verify that our passed instance was used, not the singleton from registry
        assertEquals(passedInstance, b.a)
    }

    // Test classes for #2435 / #2406
    class NamedStringConsumer(val id: String)
    class InjectedParamConsumer(val id: String, val nested: NamedStringConsumer)
    class NamedEnvComponent(val env: String)

    /**
     * #2435 - InjectedParamConsumer is built with parametersOf("event-id"), and its nested
     * NamedStringConsumer asks for get(named("customId")). The "event-id" param must not bleed
     * into that named String - a get(named(...)) always comes from the registry.
     */
    @Test
    fun qualified_dependency_is_resolved_from_registry_not_from_stacked_parameters() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single(named("customId")) { "named-id" }
                    factory { NamedStringConsumer(get(named("customId"))) }
                    factory { p -> InjectedParamConsumer(p.get(), get()) }
                },
            )
        }

        val koin = app.koin
        val consumer: InjectedParamConsumer = koin.get { parametersOf("event-id") }

        // param goes where it was asked for
        assertEquals("event-id", consumer.id)
        // but not into the named slot
        assertEquals("named-id", consumer.nested.id)
    }

    /**
     * #2406 - get(named("env")) must resolve the named single even while a String param
     * ("param-value") sits on the scope's parameter stack.
     */
    @Test
    fun qualified_string_dependency_is_not_filled_from_stacked_parameter() {
        val app = koinApplication {
            modules(
                module {
                    single(named("env")) { "PROD" }
                    single { NamedEnvComponent(get(named("env"))) }
                },
            )
        }

        val koin = app.koin
        val component: NamedEnvComponent = koin.get { parametersOf("param-value") }

        assertEquals("PROD", component.env)
    }

    /**
     * #2435, scope variant - a param stacked on a child scope must not satisfy a qualified
     * lookup either; it should fall through to the root definition via linked scopes.
     */
    @Test
    fun qualified_dependency_in_scope_is_resolved_from_linked_registry_not_from_stacked_parameters() {
        val app = koinApplication {
            modules(
                module {
                    single(named("customId")) { "named-id" }
                    scope(named("aScope")) {
                        scoped { NamedStringConsumer(get(named("customId"))) }
                    }
                },
            )
        }

        val koin = app.koin
        val scope = koin.createScope("scope-1", named("aScope"))
        val consumer: NamedStringConsumer = scope.get { parametersOf("event-id") }

        assertEquals("named-id", consumer.id)
    }
}
