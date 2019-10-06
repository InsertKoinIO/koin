package org.koin.core

import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getDefinition
import org.koin.test.hasBeenCreated
import kotlin.random.Random

const val MAX_TIME = 1000L

class MultithreadTest {

    @Test
    fun `multi thread get`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                    single { Simple.ComponentC(get()) }
                })
        }

        val rootScope = app.koin.rootScope

        val threads = arrayListOf<Thread>()
        threads.add(Thread(Runnable {
            randomSleep()
            app.koin.get<Simple.ComponentA>()
        }))
        threads.add(Thread(Runnable {
            randomSleep()
            app.koin.get<Simple.ComponentB>()
        }))
        threads.add(Thread(Runnable {
            randomSleep()
            app.koin.get<Simple.ComponentC>()
        }))

        threads.forEach { it.start() }

        val a = app.getDefinition(Simple.ComponentA::class)!!
        val b = app.getDefinition(Simple.ComponentA::class)!!
        val c = app.getDefinition(Simple.ComponentA::class)!!

        while (!a.hasBeenCreated(rootScope) && !b.hasBeenCreated(rootScope) && !c.hasBeenCreated(rootScope)) {
            Thread.sleep(100L)
        }

        assertTrue(a.hasBeenCreated(rootScope))
        assertTrue(b.hasBeenCreated(rootScope))
        assertTrue(c.hasBeenCreated(rootScope))
        app.close()
    }

    private fun randomSleep() {
        val timer = Random.nextLong(MAX_TIME)
        println("thread sleep  $timer")
        Thread.sleep(timer)
    }
}