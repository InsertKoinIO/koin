package org.koin.mp

import kotlin.math.roundToLong

actual object KoinPlatformTimeTools {
    actual fun getTimeInNanoSeconds() : Long {
        return (js("self.performance.now()") as Double * 1_000_000).roundToLong()
    }
}