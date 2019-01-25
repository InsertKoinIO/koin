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
package org.koin.android.logger

import android.util.Log
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

/**
 * Logger that uses Android Log.i
 *
 * @author - Arnaud GIULIANI
 */
class AndroidLogger(level: Level = Level.INFO) : Logger(level) {

    override fun log(level: Level, msg: MESSAGE) {
        if (this.level <= level) {
            LogOnLevel(msg)
        }
    }

    private fun LogOnLevel(msg: MESSAGE) {
        when (this.level) {
            Level.DEBUG -> Log.d(KOIN_TAG, msg)
            Level.INFO -> Log.i(KOIN_TAG, msg)
            Level.ERROR -> Log.e(KOIN_TAG, msg)
        }
    }
}