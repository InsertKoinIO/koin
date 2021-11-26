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

import android.content.ComponentCallbacks
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass


/**
 * ViewModelStoreOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> ComponentCallbacks.viewModel(
        qualifier: Qualifier? = null,
        noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
                getViewModel(qualifier, owner, parameters)
        }
}

inline fun <reified T : ViewModel> ComponentCallbacks.getViewModel(
        qualifier: Qualifier? = null,
        noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
        noinline parameters: ParametersDefinition? = null,
): T {
        return getViewModel(qualifier, T::class, owner,  parameters = parameters)
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> ComponentCallbacks.getViewModel(
        qualifier: Qualifier? = null,
        clazz: KClass<T>,
        owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
        parameters: ParametersDefinition? = null,
): T {
        val scope = getKoinScope()
        return scope.getViewModel(qualifier, owner, clazz, parameters = parameters)
}