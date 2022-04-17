package org.koin.core.time

internal abstract class TimeSource {
    /**
     * Marks a point in time on this time source in nanoseconds.
     */
    abstract fun mark(): Long
}

internal expect fun getTimeSource(): TimeSource