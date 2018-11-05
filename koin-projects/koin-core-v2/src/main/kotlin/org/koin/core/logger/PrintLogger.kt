/*
 * Copyright 2017-2018 the original author or authors.
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

import java.text.SimpleDateFormat
import java.util.*


/**
 * Logger that print on system.out
 * @author - Arnaud GIULIANI
 */
class PrintLogger(val level: Level = Level.INFO) {

    private val df = SimpleDateFormat("HH:mm:ss:SSS")

    private fun date() = df.format(Date()).toString()

    fun debug(msg: MESSAGE) {
        log(Level.DEBUG, msg)
    }

    fun info(msg: MESSAGE) {
        log(Level.INFO, msg)
    }

    fun error(msg: MESSAGE) {
        log(Level.ERROR, msg)
    }

    fun log(level: Level, msg: MESSAGE) {
        if (this.level <= level) {
            println("${date()} [$level] ${msg()}")
        }
    }
}

enum class Level {
    DEBUG, INFO, ERROR, NONE
}

typealias MESSAGE = () -> String