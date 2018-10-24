package org.koin.core.time

import org.koin.core.Koin
import kotlin.system.measureNanoTime


fun measureDuration(code : () -> Unit) = measureNanoTime(code) / 1000000.0

fun <T> logDuration(logMessage: String? = null, code: () -> T): T {
    val start = System.nanoTime()
    val result = code()
    val duration = (System.nanoTime() - start) / 1000000.0
    logMessage?.let {
        Koin.logger.debug("$logMessage in $duration ms")
    }
    return result
}