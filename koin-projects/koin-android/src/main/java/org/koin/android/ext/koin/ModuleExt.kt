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
package org.koin.android.ext.koin

import android.app.Application
import android.content.Context
import org.koin.android.error.MissingAndroidContextException
import org.koin.core.scope.Scope

const val ERROR_MSG = "Please use androidContext() function in your KoinApplication configuration."

/**
 * DSL extension - Resolve Android Context instance
 *
 * @author Arnaud Giuliani
 */
fun Scope.androidContext(): Context = try {
    get()
} catch (e: Exception) {
    throw MissingAndroidContextException("Can't resolve Context instance. $ERROR_MSG")
}

/**
 * DSL extension - Resolve Android Context instance
 *
 * @author Arnaud Giuliani
 */
fun Scope.androidApplication(): Application = try {
    get()
} catch (e: Exception) {
    throw MissingAndroidContextException("Can't resolve Application instance. $ERROR_MSG")
}
