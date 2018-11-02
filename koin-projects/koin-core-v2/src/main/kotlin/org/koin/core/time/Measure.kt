package org.koin.core.time

import org.koin.core.KoinApplication

fun <T> logDuration(logMessage: String? = null, code: () -> T): T {
    val start = System.nanoTime()
    val result = code()
    val duration = (System.nanoTime() - start) / 1000000.0
    logMessage?.let {
        KoinApplication.log("$logMessage in $duration ms")
    }
    return result
}