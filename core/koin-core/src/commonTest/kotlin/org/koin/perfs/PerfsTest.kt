package org.koin.perfs

import kotlin.test.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.*
import org.koin.core.time.Timer
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatformTools
import org.koin.test.assertDefinitionsCount

@OptIn(KoinInternalApi::class)
class PerfsTest {

    @Test
    fun empty_module_perfs() {
        val timerStart = Timer.start()
        val app = koinApplication()
        timerStart.stop()
        println("empty start in ${timerStart.getTimeInMillis()} ms")

        app.assertDefinitionsCount(0)
        app.close()
    }


    @Test
    fun perfModule400_module_perfs() {
        runPerfs()
    }

    private fun runPerfs(log: Logger = EmptyLogger()) {
        val timerStart = Timer.start()
        val app = koinApplication {
            modules(perfModule400())
        }
        timerStart.stop()
        println("perf400 - start in ${timerStart.getTimeInMillis()}")

        val koin = app.koin

        val timerRun = Timer.start()
        koin.get<Perfs.A27>()
        koin.get<Perfs.B31>()
        koin.get<Perfs.C12>()
        koin.get<Perfs.D42>()
        timerRun.stop()
        println("perf400 - executed in ${timerRun.getTimeInMillis()}")

        app.close()
    }
}