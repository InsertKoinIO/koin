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
@file:OptIn(KoinInternalApi::class)

package org.koin.core

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.extension.coroutinesEngine
import org.koin.core.module.Module

/**
 * @author Arnaud Giuliani
 */

/**
 * Load lazy modules asynchronously in parallel background coroutines.
 *
 * Each lazy module is loaded in its own coroutine job, enabling true parallel
 * initialization and significantly reducing startup time when multiple modules are used.
 *
 * Lazy<Module> are not resolved directly until their job executes, improving warmup time.
 * The coroutinesEngine() is automatically set up if needed.
 *
 * @param moduleList Vararg list of lazy modules to load in parallel
 * @param dispatcher Optional coroutine dispatcher (defaults to platform default)
 *
 * Example:
 * ```kotlin
 * startKoin {
 *     lazyModules(
 *         lazyModule { /* module 1 */ },
 *         lazyModule { /* module 2 */ }
 *     )
 * }
 * ```
 */
fun KoinApplication.lazyModules(vararg moduleList: Lazy<Module>,dispatcher: CoroutineDispatcher? = null) {
    lazyModules(moduleList.toList(),dispatcher)
}

/**
 * Load lazy modules asynchronously in parallel background coroutines.
 *
 * Each lazy module is loaded in its own coroutine job, enabling true parallel
 * initialization and significantly reducing startup time when multiple modules are used.
 *
 * Lazy<Module> are not resolved directly until their job executes, improving warmup time.
 * The coroutinesEngine() is automatically set up if needed.
 *
 * @param moduleList List of lazy modules to load in parallel
 * @param dispatcher Optional coroutine dispatcher (defaults to platform default)
 *
 * Example:
 * ```kotlin
 * val modules = listOf(
 *     lazyModule { /* module 1 */ },
 *     lazyModule { /* module 2 */ }
 * )
 * startKoin {
 *     lazyModules(modules)
 * }
 * ```
 */
fun KoinApplication.lazyModules(moduleList: List<Lazy<Module>>,dispatcher: CoroutineDispatcher? = null) {
    coroutinesEngine(dispatcher)

    moduleList.forEach { m ->
        koin.coroutinesEngine.launchStartJob {
            modules(m.value)
        }
    }
}
