package org.koin.core

import kotlin.test.assertTrue
import kotlin.test.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getInstanceFactory
import kotlin.random.Random

const val MAX_TIME = 1000L
//TODO: Need to figure out how this happens multiplatform
/*
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

        val a = app.getInstanceFactory(Simple.ComponentA::class)!!
        val b = app.getInstanceFactory(Simple.ComponentA::class)!!
        val c = app.getInstanceFactory(Simple.ComponentA::class)!!

        while (!a.isCreated() && !b.isCreated() && !c.isCreated()) {
            Thread.sleep(100L)
        }

        assertTrue(a.isCreated())
        assertTrue(b.isCreated())
        assertTrue(c.isCreated())
        app.close()
    }

    private fun randomSleep() {
        val timer = Random.nextLong(MAX_TIME)
        println("thread sleep  $timer")
        Thread.sleep(timer)
    }
}*/
