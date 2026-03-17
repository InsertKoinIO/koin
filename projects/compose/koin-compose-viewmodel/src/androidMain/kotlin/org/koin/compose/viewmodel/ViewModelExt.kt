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

package org.koin.compose.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolves a [ViewModel] instance scoped to the current Activity.
 *
 * This function retrieves a ViewModel using the current [ComponentActivity] as the ViewModelStoreOwner,
 * ensuring the ViewModel is scoped to the Activity's lifecycle.
 *
 * @param T The type of ViewModel to resolve.
 * @param qualifier Optional [Qualifier] to distinguish between multiple definitions of the same type.
 * @param key Optional key to identify the ViewModel instance.
 * @param scope The Koin [Scope] to resolve from. Defaults to the current Koin scope.
 * @param parameters Optional [ParametersDefinition] to pass dynamic parameters to the ViewModel.
 * @return The resolved ViewModel instance of type [T].
 * @throws IllegalStateException if the current Activity is not a [ComponentActivity].
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T : ViewModel> koinActivityViewModel(
    qualifier: Qualifier? = null,
    key: String? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T = koinViewModel<T>(
    qualifier = qualifier,
    viewModelStoreOwner = LocalActivity.current as? ComponentActivity ?: error("LocalActivity is not a ComponentActivity"),
    key = key,
    scope = scope,
    parameters = parameters,
)
