package org.koin.core.time

internal actual fun getTimeSource() = object: TimeSource() {
    override fun mark(): Long {
        return System.nanoTime()
    }
}