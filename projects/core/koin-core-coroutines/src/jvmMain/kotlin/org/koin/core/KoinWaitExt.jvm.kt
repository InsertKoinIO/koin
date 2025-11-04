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

import kotlinx.coroutines.runBlocking

/**
 * @author Arnaud Giuliani
 */

// Moved to Common
///**
// * Wait for Starting coroutines jobs to finish using runBlocking
// */
//fun Koin.waitAllStartJobs() {
//    runBlocking {
//        awaitAllStartJobs()
//    }
//}

/**
 * Wait for Starting coroutines jobs to run block code
 *
 * @param block
 */
fun Koin.runOnKoinStarted(block: suspend (Koin) -> Unit) {
    runBlocking {
        onKoinStarted(block)
    }
}
