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
package org.koin.androidx.viewmodel.scope

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.StateBundleParameter
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

inline fun <reified T : ViewModel> Scope.getStateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getStateViewModel(owner, T::class, qualifier, bundle, parameters)
}

fun <T : ViewModel> Scope.getStateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): T {
    val bundleOrDefault: Bundle = bundle ?: Bundle()
    return getViewModel(
        ViewModelParameter(
            clazz,
            qualifier,
            parameters,
            bundleOrDefault,
            owner.getViewModelStore(),
            owner
        )
    )
}

private fun SavedStateRegistryOwner.getViewModelStore(): ViewModelStore {
    return when (this) {
        is ViewModelStoreOwner -> this.viewModelStore
        else -> error("getStateViewModel error - Can't get ViewModelStore from $this")
    }
}

inline fun <reified T : ViewModel> Scope.stateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    noinline bundle: StateBundleParameter? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(owner, T::class, qualifier, bundle?.invoke(), parameters) }
}

fun <T : ViewModel> Scope.stateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(owner, clazz, qualifier, bundle, parameters) }
}