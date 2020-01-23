package org.koin.core.logger

/**
 * No loggings Logger
 */
class EmptyLogger : Logger(Level.ERROR) {

    override fun log(level: Level, msg: MESSAGE) {}
}