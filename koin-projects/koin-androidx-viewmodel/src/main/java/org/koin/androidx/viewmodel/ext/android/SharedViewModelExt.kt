/*
 * Copyright 2017-2020 the original author or authors.
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

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.SavedStateRegistryOwnerDefinition
import org.koin.androidx.viewmodel.scope.ViewModelStoreDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Fragment extension to help for Viewmodel
 *
 * @author Arnaud Giuliani
 */

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    qualifier: Qualifier? = null,
    noinline store: ViewModelStoreDefinition = { requireActivity().viewModelStore },
    noinline stateRegistry: SavedStateRegistryOwnerDefinition = { requireActivity() },
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(T::class, qualifier, store, stateRegistry, state, parameters)
    }
}

fun <T : ViewModel> Fragment.sharedViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    store: ViewModelStoreDefinition = { requireActivity().viewModelStore },
    stateRegistry: SavedStateRegistryOwnerDefinition = { requireActivity() },
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel(clazz, qualifier, store, stateRegistry, state, parameters) }
}

inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    qualifier: Qualifier? = null,
    noinline store: ViewModelStoreDefinition = { requireActivity().viewModelStore },
    noinline stateRegistry: SavedStateRegistryOwnerDefinition = { requireActivity() },
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(T::class, qualifier, store, stateRegistry, state, parameters)
}

fun <T : ViewModel> Fragment.getSharedViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    store: ViewModelStoreDefinition = { requireActivity().viewModelStore },
    stateRegistry: SavedStateRegistryOwnerDefinition = { requireActivity() },
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null
): T {
    return getKoin().getViewModel(clazz, qualifier, store, stateRegistry, state, parameters)
}