package org.koin.test.scope

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.NoScopeException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.AutoCloseKoinTest

class ScopeDSLTest : AutoCloseKoinTest() {

    class A(val b: B)
    class B

    @Test
    fun `use scope id from DSL`() {
        StandAloneContext.startKoin(listOf(module {
            scope("session") { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session")

        val b_1 = get<B>()

        val b_2 = get<B>()

        Assert.assertEquals(b_1, b_2)
    }

    @Test
    fun `use closed scope id from DSL`() {
        StandAloneContext.startKoin(listOf(module {
            scope("session") { B() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val session = koin.createScope("session")
        session.close()

        try {
            get<B>()
            fail()
        } catch (e: NoScopeException) {
        }
    }

    @Test
    fun `use scope id from DSL - dependency with same scope`() {
        StandAloneContext.startKoin(listOf(module {
            scope("session") { B() }
            scope("session") { A(get()) }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session")

        val b = get<B>()

        val a = get<A>()

        Assert.assertEquals(a.b, b)
    }
}