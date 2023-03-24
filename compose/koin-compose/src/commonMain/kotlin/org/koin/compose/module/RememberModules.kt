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

package org.koin.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import org.koin.compose.getKoin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module

/**
 * Load and remember Modules & run CompositionKoinModuleLoader to handle scope closure
 *
 * @param unloadOnForgotten : unload loaded modules on onForgotten event
 * @param unloadOnAbandoned : unload loaded modules on onAbandoned event
 * @param unloadModules : unload loaded modules on onForgotten or onAbandoned event
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
@Composable
inline fun rememberKoinModules(
    unloadOnForgotten : Boolean? = null,
    unloadOnAbandoned : Boolean? = null,
    unloadModules : Boolean = false,
    crossinline modules: @DisallowComposableCalls () -> List<Module> = { emptyList() }
) {
    val koin = getKoin()
    remember {
        CompositionKoinModuleLoader(modules(), koin, unloadOnForgotten?: unloadModules, unloadOnAbandoned?: unloadModules)
    }
}
