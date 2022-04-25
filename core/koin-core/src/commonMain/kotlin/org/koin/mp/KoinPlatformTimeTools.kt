package org.koin.mp

expect object KoinPlatformTimeTools {
    fun getTimeInNanoSeconds() : Long
}