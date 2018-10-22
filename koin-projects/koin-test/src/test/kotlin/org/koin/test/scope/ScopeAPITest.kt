package org.koin.test.scope

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.dsl.module.module
import org.koin.error.NoScopeException
import org.koin.error.NoScopeFoundException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.registerScopeCallback
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

class ScopeAPITest : AutoCloseKoinTest() {

    class A
    class B
    class C(val b: B)

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
            scope("session1") { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>()
        assertNotNull(b_1)

        session1_1.close()

        try {
            get<B>()
            fail()
        } catch (e: NoScopeException) {
        }
    }


    @Test
    fun `get scope instance with no scope`() {
        startKoin(listOf(module {
            scope("session1") { B() }
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
            module("mod1") {
                scope("session1") { B() }
            }
            module("mod2") {
                scope("session2") { B() }
            }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>(name = "mod1.B")

        val session1_2: Scope = koin.createScope("session2")

        val b_2 = get<B>(name = "mod2.B")

        assertNotEquals(b_1, b_2)

        session1_1.close()
        session1_2.close()
    }

    @Test
    fun `get & release from same scope`() {
        startKoin(listOf(module {
            scope("session1") { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val b_1 = get<B>()

        val session1_2: Scope = koin.getScope("session1")

        val b_2 = get<B>()

        assertEquals(b_1, b_2)

        session1_1.close()
    }

    @Test
    fun `mixed scope`() {
        startKoin(listOf(module {
            scope("session1") { B() }
            scope("session2") { C(get()) }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session1")
        koin.createScope("session2")

        val b_1 = get<B>()
        val c_1 = get<C>()

        val b_2 = get<B>()
        val c_2 = get<C>()

        assertEquals(b_1, b_2)
        assertEquals(c_1, c_2)
    }

    @Test
    fun `mixed same scope`() {
        startKoin(listOf(module {
            single { A() }
            scope("session1") { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session1_1: Scope = koin.createScope("session1")

        val a_1 = get<A>()
        val b_1 = get<B>()

        val session1_2: Scope = koin.getScope("session1")

        val a_2 = get<A>()
        val b_2 = get<B>()

        assertEquals(b_1, b_2)

        val a_3 = get<A>()
        assertEquals(a_1, a_2)
        assertEquals(a_1, a_3)

        session1_1.close()
    }

    @Test
    fun `scope close callback`() {
        startKoin(listOf(module {
            scope("session") { B() }
        }), logger = PrintLogger(showDebug = true))

        var closed: String? = null
        registerScopeCallback(object : ScopeCallback {
            override fun onClose(id: String) {
                closed = id
            }
        })

        val koin = getKoin()

        val id = "session"
        val session: Scope = koin.createScope(id)

        val b = get<B>()
        assertNotNull(b)

        session.close()

        assertEquals(id, closed)
    }

}