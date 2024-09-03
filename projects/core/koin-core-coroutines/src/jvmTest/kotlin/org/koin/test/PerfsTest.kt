package org.koin.test

import kotlinx.coroutines.runBlocking
import org.koin.core.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.waitKoinStart
import org.koin.core.logger.*
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class PerfsTest {

    @Test
    fun perfModule400_module_perfs() {
        (1..3).forEach { runPerfs() }
    }

    @Test
    fun perfModule400_lazy_module_perfs() {
        (1..3).forEach { runPerfs(isLazy = true) }
    }

    private fun runPerfs(log: Logger = EmptyLogger(), isLazy: Boolean = false) {
        val (app, startKoinAppDuration) = measureTimedValue {
            koinApplication {
                if (isLazy) {
                    lazyModules(perfModule400())
                } else {
                    modules(perfModule400().value)
                }
            }
        }

        println("perf400 - start in $startKoinAppDuration")

        val koin = app.koin

        if (isLazy) {
            koin.waitAllStartJobs()
        }

        val getDepsDuration = measureTime {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }

        println("perf400 - executed in $getDepsDuration")

        app.close()
    }

    @Test
    fun start_and_wait() {
        startKoin {
            lazyModules(perfModule400())
        }
        waitKoinStart()

        val koin = KoinPlatform.getKoin()
        assert(koin.isAllStartedJobsDone())
        koin.get<Perfs.A27>()

        stopKoin()
    }

    @Test
    fun run_after_start_coroutine() {
        startKoin {
            printLogger(Level.DEBUG)
            lazyModules(perfModule400())
        }
        runBlocking {
            KoinPlatform.getKoin().onKoinStarted { koin ->
                assert(koin.isAllStartedJobsDone())
                koin.get<Perfs.A27>()
            }
        }
        stopKoin()
    }

    @Test
    fun run_after_start() {
        startKoin {
            printLogger(Level.DEBUG)
            lazyModules(perfModule400())
        }
        KoinPlatform.getKoin().runOnKoinStarted { koin ->
            assert(koin.isAllStartedJobsDone())
            koin.get<Perfs.A27>()
        }
        stopKoin()
    }
}
