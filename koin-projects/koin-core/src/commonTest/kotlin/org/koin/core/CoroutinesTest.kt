package org.koin.core

import kotlinx.coroutines.*
import org.junit.Assert
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.getDefinition
import org.koin.test.hasBeenCreated
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class CoroutinesTest {

    @Test
    fun `KoinApp with coroutines gets`() = runBlocking {
        val app = startKoin {
            modules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                    single { Simple.ComponentC(get()) }
                })
        }
        val koin = app.koin

        val jobs = arrayListOf<Deferred<*>>()
        jobs.add(async {
            randomSleep()
            koin.get<Simple.ComponentA>()
        })
        jobs.add(async {
            randomSleep()
            koin.get<Simple.ComponentB>()
        })
        jobs.add(async {
            randomSleep()
            koin.get<Simple.ComponentC>()
        })
        jobs.awaitAll()

        val a = app.getDefinition(Simple.ComponentA::class)!!
        val b = app.getDefinition(Simple.ComponentA::class)!!
        val c = app.getDefinition(Simple.ComponentA::class)!!

        assertTrue(a.hasBeenCreated())
        assertTrue(b.hasBeenCreated())
        assertTrue(c.hasBeenCreated())

        stopKoin()
    }

    private suspend fun randomSleep() {
        val timer = Random.nextLong(MAX_TIME)
        println("thread sleep  $timer")
        delay(timer)
    }
}
