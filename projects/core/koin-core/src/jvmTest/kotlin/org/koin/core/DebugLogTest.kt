package org.koin.core

import org.junit.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import kotlin.time.measureTime

@OptIn(KoinInternalApi::class)
class DebugLogTest {

    @Test
    fun fasterDebug() {
        val koin = startKoin {
            printLogger(Level.INFO)
        }.koin

        (1..10).forEach {
            val withIfDuration = measureTime {
                if (koin.logger.isAt(Level.DEBUG)) {
                    koin.logger.debug(message { "test me" })
                }
            }

            println("with if in $withIfDuration")

            val withFctDuration = measureTime {
                koin.logger.log(Level.DEBUG) { "test me" }
            }

            println("with fct in $withFctDuration")
        }
        stopKoin()
    }

    fun message(msg: () -> String): String {
        println("resolve message")
        return msg()
    }
}
