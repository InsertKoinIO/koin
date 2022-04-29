package org.koin.mp

import kotlin.system.getTimeNanos

actual object KoinPlatformTimeTools {
    actual fun getTimeInNanoSeconds() : Long {
        return getTimeNanos()
    }
}