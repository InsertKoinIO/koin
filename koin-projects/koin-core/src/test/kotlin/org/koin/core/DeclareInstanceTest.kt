package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

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
            printLogger()
            modules(module {
                single { Simple.ComponentA() }
            })
        }.koin

        val a = Simple.ComponentA()

        try {
            koin.declare(a)
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }


    @Test
    fun `can declare a single with qualifier on the fly`() {

        val koin = koinApplication {
            printLogger()
            modules(module {
                single { Simple.ComponentA() }
            })
        }.koin

        val a = Simple.ComponentA()

        koin.declare(a, named("another_a"))

        assertEquals(a, koin.get<Simple.ComponentA>(named("another_a")))
        assertNotEquals(a, koin.get<Simple.ComponentA>())
    }

    @Test
    fun `can declare a single with secondary type on the fly`() {

        val koin = koinApplication {
            printLogger()
            modules(emptyList())
        }.koin

        val a = Simple.Component1()

        koin.declare(a, secondaryTypes = listOf(Simple.ComponentInterface1::class))

        assertEquals(a, koin.get<Simple.Component1>())
        assertEquals(a, koin.get<Simple.ComponentInterface1>())
    }

    @Test
    fun `can declare a scoped on the fly`() {

        val koin = koinApplication {
            printLogger()
            modules(module {
                scope(named("Session")) {
                    scoped { Simple.ComponentB(get()) }
                }
            })
        }.koin

        val a = Simple.ComponentA()

        val session1 = koin.createScope("session1", named("Session"))

        session1.declare(a)
        assertEquals(a, session1.get<Simple.ComponentA>())
        assertEquals(a, session1.get<Simple.ComponentB>().a)
    }

    @Test
    fun `can't declare a scoped-single on the fly`() {

        val koin = koinApplication {
            printLogger()
            modules(module {
                scope(named("Session")) {
                }
            })
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
    fun `can't declare a other scoped on the fly`() {

        val koin = koinApplication {
            printLogger()
            modules(module {
                scope(named("Session")) {
                }
            })
        }.koin

        val a = Simple.ComponentA()

        val session1 = koin.createScope("session1", named("Session"))
        val session2 = koin.createScope("session2", named("Session"))
        session1.declare(a)

        try {
            session2.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }
}