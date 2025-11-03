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
package org.koin.dsl.navigation3

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.navigation3.EntryProviderInstaller
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module._scopedInstanceFactory
import org.koin.core.module._singleInstanceFactory
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

/**
 * Declares a scoped navigation entry within a Koin scope DSL.
 *
 * This function registers a composable navigation destination that is scoped to a specific Koin scope,
 * allowing access to scoped dependencies within the composable. The route type [T] is used as both
 * the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * activityScope {
 *     viewModel { MyViewModel() }
 *     navigation<MyRoute> { route ->
 *         MyScreen(viewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param T The type representing the navigation route/destination
 * @param definition A composable function that receives the [Scope] and route instance [T] to render the destination
 * @return A [KoinDefinition] for the created [EntryProviderInstaller]
 *
 * @see Module.navigation for module-level navigation entries
 */
@KoinExperimentalAPI
@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> ScopeDSL.navigation(
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _scopedInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this
        {
            entry<T>(content = { t -> definition(scope, t) })
        }
    }, scopeQualifier)
    module.indexPrimaryType(def)
    return KoinDefinition(module, def)
}

/**
 * Declares a singleton navigation entry within a Koin module.
 *
 * This function registers a composable navigation destination as a singleton in the Koin module,
 * allowing access to module-level dependencies within the composable. The route type [T] is used
 * as both the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * module {
 *     viewModel { MyViewModel() }
 *     navigation<HomeRoute> { route ->
 *         HomeScreen(myViewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param T The type representing the navigation route/destination
 * @param definition A composable function that receives the [Scope] and route instance [T] to render the destination
 * @return A [KoinDefinition] for the created [EntryProviderInstaller]
 *
 * @see ScopeDSL.navigation for scope-level navigation entries
 */
@KoinExperimentalAPI
@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Module.navigation(
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _singleInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this
        {
            entry<T>(content = { t -> definition(scope, t) })
        }
    })
    indexPrimaryType(def)
    return KoinDefinition(this, def)
}
