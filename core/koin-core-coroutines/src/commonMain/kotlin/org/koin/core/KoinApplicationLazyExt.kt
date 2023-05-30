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
@file:OptIn(KoinInternalApi::class)

package org.koin.core

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.extension.coroutinesEngine
import org.koin.core.module.Module

/**
 * @author Arnaud Giuliani
 */

/**
 * Load asynchronously in background, a list of Lazy Module
 * uses background coroutine to load modules
 *
 * Lazy<Module> are not resolved directly, and help warmup time
 *
 * run coroutinesEngine() to setup if needed
 */
@KoinExperimentalAPI
fun KoinApplication.lazyModules(vararg list: Lazy<Module>) {
    coroutinesEngine()
    koin.coroutinesEngine.launchStartJob {
        modules(list.map { it.value })
    }
}

/**
 * Load asynchronously in background, a list of Lazy Module
 * uses background coroutine to load modules
 *
 * Lazy<Module> are not resolved directly, and help warmup time
 *
 * run coroutinesEngine() to setup if needed
 */
@KoinExperimentalAPI
fun KoinApplication.lazyModules(list: List<Lazy<Module>>) {
    coroutinesEngine()
    koin.coroutinesEngine.launchStartJob {
        modules(list.map { it.value })
    }
}

