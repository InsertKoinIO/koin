package org.koin.test.scope

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.AutoCloseKoinTest

class KoinComponentPathTest : AutoCloseKoinTest() {

    val module = module {
        module(path = "A") {
            module(path = "B") {
                single { Repository() }
                module(path = "C") {
                    scope("session1") { Presenter(get()) }
                }

                module(path = "D") {
                    scope("session2") { Presenter2(get()) }
                }
            }
        }
    }

    class Repository()
    class Presenter(val repository: Repository)
    class Presenter2(val repository: Repository)

    @Test
    fun `create views different modules & use session`() {
        startKoin(listOf(module), logger = PrintLogger(showDebug = true))

        val session1 = getKoin().createScope("session1")
        val p1 = get<Presenter>()

        val session2 = getKoin().createScope("session2")
        val p2 = get<Presenter2>()

        assertEquals(p1.repository, p2.repository)
        assertEquals(p1.repository, get<Repository>())

        assertEquals(1, session1.size())
        assertEquals(1, session2.size())

        session1.close()
        session2.close()
    }
}