package org.koin.core

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.getDefinition
import org.koin.test.hasBeenCreated
import kotlin.random.Random

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

        Assert.assertTrue(a.hasBeenCreated())
        Assert.assertTrue(b.hasBeenCreated())
        Assert.assertTrue(c.hasBeenCreated())

        stopKoin()
    }

    private suspend fun randomSleep() {
        val timer = Random.nextLong(MAX_TIME)
        println("thread sleep  $timer")
        delay(timer)
    }
}