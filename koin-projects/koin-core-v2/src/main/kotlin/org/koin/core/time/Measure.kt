package org.koin.core.time

fun measureDurationOnly(code: () -> Unit): Double {
    val start = System.nanoTime()
    code()
    return (System.nanoTime() - start) / 1000000.0
}

fun <T> measureDuration(code: () -> T): Pair<T, Double> {
    val start = System.nanoTime()
    val result = code()
    val duration = (System.nanoTime() - start) / 1000000.0
    return Pair(result, duration)
}