package org.koin.core

import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.DependencyCycleException
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class DeclareInstanceTest {

    @Test
    fun `can declare a single on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(emptyList())
        }.koin

        val a = Simple.ComponentA()

        koin.declare(a)

        assertEquals(a, koin.get<Simple.ComponentA>())
    }

    @Test
    fun `can't declare a single on the fly`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        try {
            koin.declare(a, allowOverride = false)
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can declare and override a single on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    single { Simple.MySingle(1) }
                },
            )
        }.koin

        val a = Simple.MySingle(2)

        koin.declare(a, allowOverride = true)
        assertEquals(2, koin.get<Simple.MySingle>().id)
    }

    @Test
    fun `can declare and override a single on the fly when override is set to false`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    single { Simple.MySingle(1) }
                },
            )
        }.koin

        val a = Simple.MySingle(2)

        try {
            koin.declare(a, allowOverride = false)
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can declare a single with qualifier on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        koin.declare(a, named("another_a"))

        assertEquals(a, koin.get<Simple.ComponentA>(named("another_a")))
        assertNotEquals(a, koin.get<Simple.ComponentA>())
    }

    @Test
    fun `can declare and override a single with qualifier on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    single { Simple.ComponentA() }
                    single(named("another_a")) { Simple.ComponentA() }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        koin.declare(a, named("another_a"), allowOverride = true)

        assertEquals(a, koin.get<Simple.ComponentA>(named("another_a")))
        assertNotEquals(a, koin.get<Simple.ComponentA>())
    }

    @Test
    fun `can declare a single with secondary type on the fly`() {
        val koin = koinApplication {
            printLogger()
        }.koin

        val a = Simple.Component1()

        koin.declare(a, secondaryTypes = listOf(Simple.ComponentInterface1::class))

        assertEquals(a, koin.get<Simple.Component1>())
        assertEquals(a, koin.get<Simple.ComponentInterface1>())
    }

    @Test
    fun `can override a single on the fly`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(emptyList())
        }.koin

        val a = Simple.Component1()
        val b = Simple.Component1()

        koin.declare(a)
        koin.declare(b, allowOverride = true)

        assertEquals(b, koin.get<Simple.Component1>())
    }

    @Test
    fun `can declare and override a single with secondary type on the fly`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(emptyList())
        }.koin

        val a = Simple.Component1()
        val b = Simple.Component1()

        koin.declare(a, secondaryTypes = listOf(Simple.ComponentInterface1::class))
        koin.declare(b, secondaryTypes = listOf(Simple.ComponentInterface1::class), allowOverride = true)

        assertEquals(b, koin.get<Simple.Component1>())
        assertEquals(koin.get<Simple.Component1>(), koin.get<Simple.Component1>())
        assertEquals(b, koin.get<Simple.ComponentInterface1>())
    }

    @Test
    fun `can declare a scoped on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    scope(named("Session")) {
                        scoped { Simple.ComponentB(get()) }
                    }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        val session1 = koin.createScope("session1", named("Session"))

        session1.declare(a)
        assertEquals(a, session1.get<Simple.ComponentA>())
        assertEquals(a, session1.get<Simple.ComponentB>().a)
    }

    @Test
    fun `can declare a scoped on the fly with primary type`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    scope(named("Session")) {
                        scoped { B() }
                    }
                },
            )
        }.koin

        val a = Simple.Component2()

        val session1 = koin.createScope("session1", named("Session"))

        session1.declare<Simple.ComponentInterface1>(a, named("another_a"))
        assertEquals(a, session1.get<Simple.ComponentInterface1>(named("another_a")))
    }

    @Test
    fun `can't declare a scoped-single on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    scope(named("Session")) {
                        scoped { B() }
                    }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        val session1 = koin.createScope("session1", named("Session"))
        session1.declare(a)

        try {
            koin.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can declare a other scoped on the fly`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    scope(named("Session")) {
                        scoped { B() }
                    }
                },
            )
        }.koin

        val a = Simple.ComponentA()

        val session1 = koin.createScope("session1", named("Session"))
        val session2 = koin.createScope("session2", named("Session"))
        session1.declare(a, allowOverride = false)

        session2.get<Simple.ComponentA>()
    }

    @Test
    fun `avoid to start eager instances`() {
        var count = 0
       koinApplication {
            modules(
                module {
                    single(createdAtStart = true){
                        count++
                        Simple.ComponentA()
                    }
                },
            )
        }

        assertEquals(1,count)
        count = 0

        koinApplication(createEagerInstances = false) {
            modules(
                module {
                    single(createdAtStart = true){
                        count++
                        Simple.ComponentA()
                    }
                },
            )
        }
        assertEquals(0,count)
    }

    @Test
    fun `detect dependency cycle single`() {

        val koin = koinApplication {
            modules(
                module {
                    single { Simple.DependencyCycle1(get()) }
                    single { Simple.DependencyCycle2(get()) }
                    single { Simple.DependencyCycle3(get()) }
                },
            )
        }.koin


        try {
            koin.get<Simple.DependencyCycle1>()
            fail()
        } catch (e: InstanceCreationException) {

            // one of the 'causes' must be DependencyCycleException, ensure there is one
            val causeStack = mutableListOf<Throwable>()
            var current = e.cause
            while (true) {
                if (current == null) break
                causeStack.add(current)
                current = current.cause
            }

            assertTrue(causeStack.any { it is DependencyCycleException })
        }
        koin.close()
    }

    @Test
    fun `detect dependency cycle scoped`() {
        val scopeKey = named("_test_scope_")
        val scopeId = "_test_scope_id_"

        val koin = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        scoped { Simple.DependencyCycle1(get()) }
                        scoped { Simple.DependencyCycle2(get()) }
                        scoped { Simple.DependencyCycle3(get()) }
                    }
                },
            )
        }.koin


        val scope = koin.createScope(scopeId, scopeKey)
        try {
            scope.get<Simple.DependencyCycle1>()
            fail()
        } catch (e: InstanceCreationException) {

            // one of the 'causes' must be DependencyCycleException, ensure there is one
            val causeStack = mutableListOf<Throwable>()
            var current = e.cause
            while (true) {
                if (current == null) break
                causeStack.add(current)
                current = current.cause
            }

            assertTrue(causeStack.any { it is DependencyCycleException })
        }
        koin.close()
    }
}
