package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.NoScopeException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

/**
 * Scope DSL Test using experimental functions [scope] and [scopeBy]
 * @see org.koin.test.scope.ScopeDSLTest
 */
class ScopeAutoBuilderDSLTest : AutoCloseKoinTest() {

    interface IB
    class A(val b: IB)
    class B : IB

    @Test
    fun `use scope id from DSL`() {
        StandAloneContext.startKoin(listOf(module {
            scopeBy<IB, B>("session")
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session")

        val b_1 = get<IB>()
        val b_2 = get<IB>()

        Assert.assertEquals(b_1, b_2)
    }

    @Test
    fun `use closed scope id from DSL`() {
        StandAloneContext.startKoin(listOf(module {
            scope<B>("session")
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
            scopeBy<IB, B>("session")
            scope<A>("session")
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session")

        val b = get<IB>()
        val a = get<A>()

        Assert.assertEquals(a.b, b)
    }

    @Test
    fun `use scope id and name from DSL`() {
        StandAloneContext.startKoin(listOf(module {
            scopeBy<IB, B>("session", "B1")
            scopeBy<IB, B>("session", "B2")
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        koin.createScope("session")

        val b_1 = get<IB>("B1")
        val b_2 = get<IB>("B2")

        Assert.assertNotEquals(b_1, b_2)
    }
}