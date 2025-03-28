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
@file:Suppress("DeprecatedCallableAddReplaceWith")

package org.koin.androidx.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.BundleDefinition
import org.koin.viewmodel.defaultExtras

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@Deprecated(
    "ViewModelLazy API is not supported by Jetpack Compose 1.1+. Please use koinViewModel()",
    level = DeprecationLevel.ERROR
)
@Composable
fun defaultExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras = defaultExtras(viewModelStoreOwner)

@OptIn(KoinInternalApi::class)
@Deprecated(
    "ViewModelLazy API is not supported by Jetpack Compose 1.1+. Please use koinViewModel()",
    replaceWith = ReplaceWith("koinViewModel()","org.koin.compose.viewmodel"),
    level = DeprecationLevel.ERROR
)
@Composable
inline fun <reified T : ViewModel> viewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null
): ViewModelLazy<T> = koinViewModel(qualifier,owner,scope = scope, parameters = parameters)

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@Deprecated("getStateViewModel is now deprecated. Use koinViewModel() instead.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("koinViewModel()","org.koin.compose.viewmodel"))
@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T : ViewModel> getStateViewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline state: BundleDefinition,
    noinline parameters: ParametersDefinition? = null,
): T = TODO("function is deprecated")