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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelStoreOwnerProducer
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

//TODO Clean up ViewModelOwnerDefinition

/**
 * ViewModelStoreOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

@OptIn(KoinInternalApi::class)
inline fun <reified T : ViewModel> Fragment.viewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelStoreOwnerProducer = { this },
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return viewModels(ownerProducer = owner) {
        getViewModelFactory<T>(owner(), qualifier, parameters, scope = getKoinScope())
    }
}

inline fun <reified T : ViewModel> Fragment.getViewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelStoreOwnerProducer = { this },
    noinline parameters: ParametersDefinition? = null,
): T {
    return viewModel<T>(qualifier, owner, parameters).value
}