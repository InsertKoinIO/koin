/*
 * Copyright 2017-present the original author or authors.
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
package org.koin.android.java

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.logger.Level

/**
 * Java Compat utility
 */
object KoinAndroidApplication {

    /**
     * Create Koin Application with Android context - For Java compat
     */
    @JvmStatic
    @JvmOverloads
    fun create(context: Context, androidLoggerLevel: Level = Level.INFO): KoinApplication {
        return KoinApplication.init().androidContext(context).androidLogger(androidLoggerLevel)
    }
}