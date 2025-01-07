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

package org.koin.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.compose.currentKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.defaultExtras
import org.koin.viewmodel.resolveViewModel

/*
    Ported directly from Android side. Waiting more feedback
 */

/**
 * Resolve ViewModel instance with Navigation NavBackStackEntry as extras parameters
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Deprecated("koinViewModel() can be used instead of koinNavViewModel(), as it's embedding nav backstack arguments")
@Composable
inline fun <reified T : ViewModel> koinNavViewModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    extras: CreationExtras = defaultExtras(viewModelStoreOwner),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T {
    return resolveViewModel(
        T::class, viewModelStoreOwner.viewModelStore, key, extras, qualifier, scope, parameters
    )
}