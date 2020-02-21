package org.koin.core.logger

/**
 * No loggings Logger
 */
class EmptyLogger : Logger(Level.NONE) {

    override fun log(level: Level, msg: MESSAGE) {
        System.err.println("should not see this - $level - $msg")
    }
}