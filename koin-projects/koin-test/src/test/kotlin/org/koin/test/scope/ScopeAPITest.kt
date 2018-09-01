package org.koin.test.scope

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.module
import org.koin.error.ClosedScopeException
import org.koin.error.NoScopeException
import org.koin.error.NoScopeFoundException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.AutoCloseKoinTest

class ScopeAPITest : AutoCloseKoinTest() {

    class A
    class B

    @Test
    fun `create get & close scope`() {
        startKoin(listOf())

        val koin = getKoin()

        val session1: Scope = koin.createScope("session1")
        val session1_2: Scope = koin.getScope("session1")

        assertEquals(session1, session1_2)

        // single dans un scope ?
        session1.close()

        try {
            koin.getScope("session1")
            fail()
        } catch (e: NoScopeFoundException) {

        }
    }

    @Test
    fun `get instance with closed scope`() {
        startKoin(listOf(module {
            scope { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>(scope = session1_1)

        session1_1.close()

        try {
            get<B>(scope = session1_1)
            fail()
        } catch (e: ClosedScopeException) {
        }
    }


    @Test
    fun `get scope instance with no scope`() {
        startKoin(listOf(module {
            scope { B() }
        }))
        try {
            get<B>()
            fail()
        } catch (e: NoScopeException) {
        }
    }

    @Test
    fun `get from default scope`() {
        startKoin(listOf(module {
            single { B() }
        }))

        val b = get<B>()
        assertNotNull(b)
    }

    @Test
    fun `get & release from diff scope`() {
        startKoin(listOf(module {
            scope { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>(scope = session1_1)

        val session1_2: Scope = koin.createScope("session2")

        val b_2 = get<B>(scope = session1_2)

        assertNotEquals(b_1, b_2)

        session1_1.close()
        session1_2.close()
    }

    @Test
    fun `get & release from same scope`() {
        startKoin(listOf(module {
            scope { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>(scope = session1_1)

        val session1_2: Scope = koin.getScope("session1")

        val b_2 = get<B>(scope = session1_2)

        assertEquals(b_1, b_2)

        session1_1.close()
    }

    @Test
    fun `mixed single & scope`() {
        startKoin(listOf(module {
            single { A() }
            scope { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val a_1 = get<A>(scope = session1_1)
        val b_1 = get<B>(scope = session1_1)

        val session1_2: Scope = koin.createScope("session2")

        val a_2 = get<A>(scope = session1_2)
        val b_2 = get<B>(scope = session1_2)

        assertNotEquals(b_1, b_2)

        val a_3 = get<A>()
        assertEquals(a_1, a_2)
        assertEquals(a_1, a_3)

        session1_1.close()
    }

    @Test
    fun `mixed same scope`() {
        startKoin(listOf(module {
            single { A() }
            scope { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val a_1 = get<A>(scope = session1_1)
        val b_1 = get<B>(scope = session1_1)

        val session1_2: Scope = koin.getScope("session1")

        val a_2 = get<A>(scope = session1_2)
        val b_2 = get<B>(scope = session1_2)

        assertEquals(b_1, b_2)

        val a_3 = get<A>()
        assertEquals(a_1, a_2)
        assertEquals(a_1, a_3)

        session1_1.close()
    }

}