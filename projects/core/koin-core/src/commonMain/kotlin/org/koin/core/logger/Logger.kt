/*
 * Copyright 2017-Present the original author or authors.
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

    abstract fun display(level: Level, msg: MESSAGE)

    fun debug(msg: MESSAGE) {
        log(Level.DEBUG, msg)
    }

    fun info(msg: MESSAGE) {
        log(Level.INFO, msg)
    }

    fun warn(msg: MESSAGE) {
        log(Level.WARNING, msg)
    }

    fun error(msg: MESSAGE) {
        log(Level.ERROR, msg)
    }

    inline fun isAt(lvl: Level): Boolean = this.level <= lvl

    fun log(lvl: Level, msg: String) {
        if (isAt(lvl)) display(lvl, msg)
    }

    fun log(lvl: Level, msg: () -> String) {
        if (isAt(lvl)) display(lvl, msg())
    }
}

const val KOIN_TAG = "[Koin]"

enum class Level {
    DEBUG, INFO, WARNING, ERROR, NONE
}

typealias MESSAGE = String
