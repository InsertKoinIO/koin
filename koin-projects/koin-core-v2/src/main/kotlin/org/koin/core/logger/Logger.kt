package org.koin.core.logger

abstract class Logger {

    var level: Level = Level.INFO

    abstract fun log(level : Level, msg : MESSAGE)

    fun debug(msg: MESSAGE) {
        log(Level.DEBUG, msg)
    }

    fun info(msg: MESSAGE) {
        log(Level.INFO, msg)
    }

    fun error(msg: MESSAGE) {
        log(Level.ERROR, msg)
    }
}

enum class Level {
    DEBUG, INFO, ERROR, NONE
}

typealias MESSAGE = () -> String