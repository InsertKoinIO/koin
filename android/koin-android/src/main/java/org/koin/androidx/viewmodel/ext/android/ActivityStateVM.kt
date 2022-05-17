/*
 * Copyright 2017-2021 the original author or authors.
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
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * ComponentActivity extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
inline fun <reified T : ViewModel> ComponentActivity.stateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> {
    val scope = getKoinScope()
    return viewModels {
        getViewModelFactory<T>(this, qualifier, parameters, state = state, scope = scope)
    }
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> ComponentActivity.stateViewModel(
    qualifier: Qualifier? = null,
    state: BundleDefinition = emptyState(),
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    val scope = getKoinScope()
    return ViewModelLazy(clazz, { viewModelStore }){
        getViewModelFactory(this, clazz, qualifier, parameters, state = state, scope = scope)
    }
}

inline fun <reified T : ViewModel> ComponentActivity.getStateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): T {
    return stateViewModel<T>(qualifier, state, parameters).value
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> ComponentActivity.getStateViewModel(
    qualifier: Qualifier? = null,
    state: BundleDefinition = emptyState(),
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null,
): T {
    return stateViewModel(qualifier, state,clazz, parameters).value
}