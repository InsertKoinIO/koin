package org.koin.core.time

import kotlin.system.getTimeNanos

internal actual fun getTimeSource() = object: TimeSource() {
    override fun mark(): Long {
        return getTimeNanos()
    }
}