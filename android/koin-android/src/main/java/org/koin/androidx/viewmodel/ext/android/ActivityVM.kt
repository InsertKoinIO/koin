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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

//TODO Clean up ViewModelOwnerDefinition in 3.2

/**
 * ViewModelStoreOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

@OptIn(KoinInternalApi::class)
inline fun <reified T : ViewModel> ComponentActivity.viewModel(
        qualifier: Qualifier? = null,
        //TODO Not needed - just internal
        noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
        val scope = getKoinScope()
        return viewModels {
                getViewModelFactory<T>(owner, qualifier, parameters, scope = scope)
        }
}

inline fun <reified T : ViewModel> ComponentActivity.getViewModel(
        qualifier: Qualifier? = null,
        //TODO Not needed - just internal
        noinline owner : ViewModelOwnerDefinition = { ViewModelOwner.from(this as ViewModelStoreOwner, this as? SavedStateRegistryOwner) },
        noinline parameters: ParametersDefinition? = null,
): T {
        return viewModel<T>(qualifier, owner, parameters).value
}