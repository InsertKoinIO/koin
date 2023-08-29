package org.koin.mp

import kotlin.time.TimeSource

actual object KoinPlatformTimeTools {
    actual fun getTimeInNanoSeconds(): Long {
        return TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds
    }
}