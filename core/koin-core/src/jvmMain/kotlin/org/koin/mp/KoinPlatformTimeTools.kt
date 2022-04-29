package org.koin.mp

actual object KoinPlatformTimeTools {
    actual fun getTimeInNanoSeconds() : Long {
        return System.nanoTime()
    }
}