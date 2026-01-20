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
package org.koin.mp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import kotlin.coroutines.CoroutineContext

/**
 * Platform-specific coroutines tools for Koin.
 *
 * Provides multiplatform abstractions for coroutine operations that have
 * platform-specific implementations.
 *
 * @author Arnaud Giuliani
 */
@KoinInternalApi
expect object KoinPlatformCoroutinesTools {
    /**
     * Get the default coroutine dispatcher for the current platform.
     *
     * @return Platform-specific default dispatcher (typically Dispatchers.Default)
     */
    fun defaultCoroutineDispatcher(): CoroutineDispatcher

    /**
     * Execute a blocking coroutine on the current platform.
     *
     * Platform implementations:
     * - JVM/Native: Uses kotlinx.coroutines.runBlocking for true blocking behavior
     * - JS: Uses GlobalScope.promise with a warning (true blocking not supported)
     *
     * @param context The coroutine context to use
     * @param block The suspend block to execute
     * @return The result of the suspend block
     */
    fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T
}
