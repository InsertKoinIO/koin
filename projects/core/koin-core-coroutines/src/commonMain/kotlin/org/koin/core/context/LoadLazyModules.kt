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
package org.koin.core.context

import org.koin.core.module.Module
import org.koin.mp.KoinPlatformTools

/**
 * Starter function to help start Koin context with default context parameters
 *
 * @author Arnaud Giuliani
 */

/**
 * load Koin module in global Koin context
 */
fun loadKoinModules(module: Lazy<Module>) = KoinPlatformTools.defaultContext().loadKoinModules(module.value)

/**
 * unload Koin module from global Koin context
 *
 * No-op if the lazy module has not been initialized yet, since in that case
 * no definitions were ever registered for it.
 *
 * @author Chris Paleopanos
 */
fun unloadKoinModules(module: Lazy<Module>) {
    if (module.isInitialized()) {
        KoinPlatformTools.defaultContext().unloadKoinModules(module.value)
    }
}

/**
 * unload Koin modules from global Koin context
 *
 * Lazy modules that have not been initialized yet are skipped, since in that
 * case no definitions were ever registered for them.
 *
 * @author Chris Paleopanos
 */
fun unloadKoinModules(modules: List<Lazy<Module>>) {
    val realizedModules = modules.filter { it.isInitialized() }.map { it.value }
    if (realizedModules.isNotEmpty()) {
        KoinPlatformTools.defaultContext().unloadKoinModules(realizedModules)
    }
}
