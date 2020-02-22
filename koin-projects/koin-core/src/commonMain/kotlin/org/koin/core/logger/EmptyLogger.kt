package org.koin.core.logger

import org.koin.mp.PlatformTools

/**
 * No loggings Logger
 */
class EmptyLogger : Logger(Level.NONE) {

    override fun log(level: Level, msg: MESSAGE) {
        PlatformTools.printLog(level, "should not see this - $level - $msg")
    }
}