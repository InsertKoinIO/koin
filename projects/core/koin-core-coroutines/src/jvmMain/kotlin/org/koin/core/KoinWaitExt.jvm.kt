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
 * JVM-specific extension functions for Koin coroutines operations.
 *
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
 * Wait for all lazy module start jobs to complete, then run the given block.
 *
 * This JVM-only function blocks the current thread until all lazy modules have
 * finished loading, then executes the provided suspend block with the Koin instance.
 *
 * Useful for scenarios where you need to perform actions after Koin is fully initialized,
 * such as running startup tasks or initializing services that depend on all modules being loaded.
 *
 * @param block Suspend lambda that receives the Koin instance once all start jobs complete
 *
 * Example:
 * ```kotlin
 * startKoin {
 *     lazyModules(myLazyModule1, myLazyModule2)
 * }
 * KoinPlatform.getKoin().runOnKoinStarted { koin ->
 *     // All modules are now loaded
 *     koin.get<StartupService>().initialize()
 * }
 * ```
 *
 * @see waitAllStartJobs for just waiting without executing code
 * @see onKoinStarted for the suspend version
 */
fun Koin.runOnKoinStarted(block: suspend (Koin) -> Unit) {
    runBlocking {
        onKoinStarted(block)
    }
}
