/*
 * Copyright 2017-2023 the original author or authors.
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
 * Logger that uses the native Android Logger
 *
 * @author - Arnaud GIULIANI
 */
class AndroidLogger(level: Level = Level.INFO) : Logger(level) {

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> Log.d(KOIN_TAG, msg)
            Level.INFO -> Log.i(KOIN_TAG, msg)
            Level.WARNING -> Log.w(KOIN_TAG, msg)
            Level.ERROR -> Log.e(KOIN_TAG, msg)
            else -> Log.e(KOIN_TAG, msg)
        }
    }
}