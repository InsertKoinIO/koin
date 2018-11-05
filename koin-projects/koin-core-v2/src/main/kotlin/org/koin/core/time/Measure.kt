package org.koin.core.time

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level

fun <T> logDuration(logMessage: String, level: Level = Level.DEBUG, code: () -> T): T {
    val start = System.nanoTime()
    val result = code()
    val duration = (System.nanoTime() - start) / 1000000.0
    logger.log(level) { "$logMessage in $duration ms" }
    return result
}