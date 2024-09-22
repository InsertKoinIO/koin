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
package org.koin.core

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.extension.coroutinesEngine

/**
 * @author Arnaud Giuliani
 */

/**
 * Wait for Starting coroutines jobs to finish
 */
@OptIn(KoinInternalApi::class)
suspend fun Koin.awaitAllStartJobs() {
    coroutinesEngine.awaitAllStartJobs()
}

/**
 * Wait for Starting coroutines jobs to run block code
 *
 * @param block
 */
suspend fun Koin.onKoinStarted(block: suspend (Koin) -> Unit) {
    awaitAllStartJobs()
    block(this)
}

/**
 * Indicates if all start jobs have been done
 */
@OptIn(KoinInternalApi::class)
fun Koin.isAllStartedJobsDone(): Boolean {
    return coroutinesEngine.startJobs.none { it.isActive }
}
