package org.koin.test.scope

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.NoScopeFoundException
import org.koin.log.PrintLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class KoinComponentPathTest : AutoCloseKoinTest() {

    val module1 = module {
        module("org.koin") {
            single { Repository() }

            module("view") {
                scope { Presenter(get()) }
            }
        }
    }

    val module2 = module {
        module(path = "A") {
            module(path = "B") {
                single { Repository() }
                module(path = "C") {
                    scope { Presenter(get()) }
                }

                module(path = "D") {
                    scope { Presenter2(get()) }
                }
            }
        }
    }

    class Repository()
    class Presenter(val repository: Repository)
    class Presenter2(val repository: Repository)

    class View1 : KoinComponent {

        val sessionView1 = getKoin().createScope("SessionView1")

        val repository by inject<Repository>()
        val presenter by inject<Presenter>(scope = sessionView1)

        fun destroy() {
            sessionView1.close()
        }
    }

    class View2 : KoinComponent {
        val sessionView2 = getKoin().createScope("SessionView2")

        val repository by inject<Repository>()
        val presenter by inject<Presenter>(scope = sessionView2)

        fun destroy() {
            sessionView2.close()
        }
    }

    @Test
    fun `create view & use session`() {
        startKoin(listOf(module1), logger = PrintLogger(showDebug = true))

        val view1 = View1()
        val sessionView1 = view1.sessionView1
        Assert.assertEquals(view1.presenter, get<Presenter>(scope = sessionView1))
        Assert.assertEquals(view1.repository, get<Repository>())
        Assert.assertEquals(view1.repository, get<Repository>(scope = sessionView1))

        assertEquals(1, sessionView1.size())

        view1.destroy()

        assertEquals(0, sessionView1.size())
        assertTrue(sessionView1.isClosed)
    }

    @Test
    fun `create views & use session`() {
        startKoin(listOf(module1), logger = PrintLogger(showDebug = true))

        val view1 = View1()
        val sessionView1 = view1.sessionView1
        Assert.assertEquals(view1.presenter, get<Presenter>(scope = sessionView1))
        Assert.assertEquals(view1.repository, get<Repository>())
        Assert.assertEquals(view1.repository, get<Repository>(scope = sessionView1))

        assertEquals(1, sessionView1.size())

        val view2 = View2()
        val sessionView2 = view2.sessionView2
        Assert.assertEquals(view2.presenter, get<Presenter>(scope = sessionView2))
        Assert.assertEquals(view2.repository, get<Repository>())
        Assert.assertEquals(view2.repository, get<Repository>(scope = sessionView2))

        Assert.assertEquals(view1.repository, view2.repository)

        assertEquals(1, sessionView2.size())


        view1.destroy()

        assertEquals(0, sessionView1.size())
        assertTrue(sessionView1.isClosed)

        view2.destroy()

        assertEquals(0, sessionView2.size())
        assertTrue(sessionView2.isClosed)

        try {
            getKoin().getScope(sessionView1.id)
        } catch (e: NoScopeFoundException) {
        }

        try {
            getKoin().getScope(sessionView2.id)
        } catch (e: NoScopeFoundException) {
        }

    }

    @Test
    fun `create views different modules & use session`() {
        startKoin(listOf(module2), logger = PrintLogger(showDebug = true))

        val session1 = getKoin().createScope("session1")
        val p1 = get<Presenter>(scope = session1)

        val session2 = getKoin().createScope("session2")
        val p2 = get<Presenter2>(scope = session2)

        assertEquals(p1.repository, p2.repository)
        assertEquals(p1.repository, get<Repository>())

        assertEquals(1, session1.size())
        assertEquals(1, session2.size())

        session1.close()
        session2.close()
    }
}