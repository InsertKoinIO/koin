package org.koin.core.time

import org.koin.core.annotation.KoinInternalApi
import org.koin.mp.KoinPlatformTimeTools

@KoinInternalApi
class Timer {
    private var _startTimeNanoSeconds: Long = 0
    private var _endTimeNanoSeconds: Long = 0
    private var _resultNanoSeconds: Long = 0

    private fun start() {
        _startTimeNanoSeconds = KoinPlatformTimeTools.getTimeInNanoSeconds()
    }

    fun stop() {
        _endTimeNanoSeconds = KoinPlatformTimeTools.getTimeInNanoSeconds()
        _resultNanoSeconds = (_endTimeNanoSeconds - _startTimeNanoSeconds)
    }

    fun getTimeInMillis(): TimeInMillis = _resultNanoSeconds / NANO_TO_MILLI
    fun getTimeInNanos(): TimeInNanos = _resultNanoSeconds

    companion object {

        fun start(): Timer {
            val timer = Timer()
            timer.start()
            return timer
        }

        val NANO_TO_MILLI = 1_000_000.0
    }
}

typealias TimeInMillis = Double
typealias TimeInNanos = Long