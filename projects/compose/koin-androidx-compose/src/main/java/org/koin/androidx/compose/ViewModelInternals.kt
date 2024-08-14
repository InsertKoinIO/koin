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
import org.koin.androidx.viewmodel.ext.android.toExtras
import org.koin.androidx.viewmodel.resolveViewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@Composable
fun defaultExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras = when {
    viewModelStoreOwner is HasDefaultViewModelProviderFactory -> viewModelStoreOwner.defaultViewModelCreationExtras
    else -> CreationExtras.Empty
}

@OptIn(KoinInternalApi::class)
@Deprecated(
    "ViewModelLazy API is not supported by Jetpack Compose 1.1+. Please use koinViewModel()",
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
): ViewModelLazy<T> = error("ViewModelLazy API is not supported by Jetpack Compose 1.1+")

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@Deprecated("Use koinViewModel() instead, with CreationExtras API")
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
): T {
    return resolveViewModel(
        T::class,
        owner.viewModelStore,
        extras = state().toExtras(owner) ?: CreationExtras.Empty, qualifier = qualifier, scope = scope, parameters = parameters
    )
}