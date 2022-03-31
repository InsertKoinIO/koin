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

import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwner.Companion.from
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
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
inline fun <reified T : ViewModel> Fragment.stateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    //TODO Clean up
    noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> {
    val scope = getKoinScope()
    return viewModels(ownerProducer = {owner().storeOwner}) {
        getViewModelFactory<T>(owner, qualifier, parameters, state = state, scope = scope)
    }
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> Fragment.stateViewModel(
    qualifier: Qualifier? = null,
    clazz: KClass<T>,
    state: BundleDefinition = emptyState(),
    //TODO Clean up
    owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    val scope = getKoinScope()
    return createViewModelLazy(clazz, { viewModelStore }){
        getViewModelFactory(owner, clazz, qualifier, parameters, state = state, scope = scope)
    }
}

inline fun <reified T : ViewModel> Fragment.getStateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
    noinline parameters: ParametersDefinition? = null,
): T {
    return stateViewModel<T>(qualifier, state, owner,parameters).value
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> Fragment.getStateViewModel(
    qualifier: Qualifier? = null,
    clazz: KClass<T>,
    state: BundleDefinition = emptyState(),
    owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
    parameters: ParametersDefinition? = null,
): T {
    return stateViewModel(qualifier,  clazz, state, owner,parameters).value
}