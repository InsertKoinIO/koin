package org.koin.mp

import org.koin.core.time.getTimeSource

actual object KoinPlatformTimeTools {

    actual fun getTimeInNanoSeconds(): Long {
        return getTimeSource().markNow()
    }
}
