package org.koin.test

import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.waitKoinStart
import org.koin.core.isAllStartedJobsDone
import org.koin.core.lazyModules
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.onKoinStarted
import org.koin.core.runOnKoinStarted
import org.koin.core.time.Timer
import org.koin.core.waitAllStartJobs
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatform
import kotlin.test.Test

class PerfsTest {

    @Test
    fun perfModule400_module_perfs() {
        for (index in 1..3) {
            runPerfs()
        }
    }

    @Test
    fun perfModule400_lazy_module_perfs() {
        for (index in 1..3) {
            runPerfs(isLazy = true)
        }
    }

    private fun runPerfs(log: Logger = EmptyLogger(), isLazy: Boolean = false) {
        val timerStart = Timer.start()
        val app = koinApplication {
            if (isLazy) {
                lazyModules(perfModule400())
            } else {
                modules(perfModule400().value)
            }
        }
        timerStart.stop()
        println("perf400 - start in ${timerStart.getTimeInMillis()}")

        val koin = app.koin

        if (isLazy) {
            koin.waitAllStartJobs()
        }

        val timerRun = Timer.start()
        koin.get<Perfs.A27>()
        koin.get<Perfs.B31>()
        koin.get<Perfs.C12>()
        koin.get<Perfs.D42>()
        timerRun.stop()
        println("perf400 - executed in ${timerRun.getTimeInMillis()}")

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
