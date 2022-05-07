/*
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core.logger

/**
 * Abstract Koin Logger
 *
 * @author Arnaud Giuliani
 */
abstract class Logger(var level: Level = Level.INFO) {

    abstract fun log(level: Level, msg: MESSAGE)

    private fun doLog(level: Level, msg: MESSAGE) {
        if (isAt(level)) {
            log(level, msg)
        }
    }

    fun debug(msg: MESSAGE) {
        doLog(Level.DEBUG, msg)
    }

    fun info(msg: MESSAGE) {
        doLog(Level.INFO, msg)
    }

    fun error(msg: MESSAGE) {
        doLog(Level.ERROR, msg)
    }

    fun isAt(level: Level): Boolean = this.level <= level

    inline fun log(level: Level, msg: () -> MESSAGE) {
        if (isAt(level)) {
            log(level, msg())
        }
    }
}

const val KOIN_TAG = "[Koin]"

/**
 * Log Level
 */
enum class Level {
    DEBUG, INFO, ERROR, NONE
}

typealias MESSAGE = String