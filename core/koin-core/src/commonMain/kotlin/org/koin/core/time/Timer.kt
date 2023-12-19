package org.koin.core.time

import org.koin.mp.KoinPlatformTimeTools
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Timer {

    val start: Duration = KoinPlatformTimeTools.getTimeInNanoSeconds().toDuration(DurationUnit.NANOSECONDS)
    var end: Duration = ZERO
        private set
    private var time: Duration = ZERO

    fun stop() {
        if (end == ZERO) {
            end = KoinPlatformTimeTools.getTimeInNanoSeconds().toDuration(DurationUnit.NANOSECONDS)
            time = end - start
        }
    }

    fun getTimeInSeconds() = time.toDouble(DurationUnit.SECONDS)
    fun getTimeInMillis() = time.toDouble(DurationUnit.MILLISECONDS)
    fun getTimeInNanos() = time.toDouble(DurationUnit.NANOSECONDS)

    companion object {
        const val NANO_TO_MILLI = 1_000_000.0
        fun start(): Timer = Timer()
    }
}
