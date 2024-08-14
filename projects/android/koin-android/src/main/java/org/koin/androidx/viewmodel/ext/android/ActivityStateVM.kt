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
package org.koin.androidx.viewmodel.ext.android

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.resolveViewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * ComponentActivity extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
@Deprecated("Use ComponentActivity.viewModel() with extras: CreationExtras")
@MainThread
inline fun <reified T : ViewModel> ComponentActivity.stateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getStateViewModel(qualifier, state, parameters)
    }
}

@Deprecated("Use ComponentActivity.getViewModel() with extras: CreationExtras")
@OptIn(KoinInternalApi::class)
@MainThread
inline fun <reified T : ViewModel> ComponentActivity.getStateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): T {
    return resolveViewModel(
        T::class,
        viewModelStore,
        extras = state().toExtras(this) ?: this.defaultViewModelCreationExtras,
        qualifier = qualifier,
        parameters = parameters,
        scope = getKoinScope()
    )
}
