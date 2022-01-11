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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelOwner.Companion.from
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * ComponentActivity extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> Fragment.sharedStateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getStateViewModel(qualifier, state, parameters)
    }
}

fun <T : ViewModel> Fragment.sharedStateViewModel(
    qualifier: Qualifier? = null,
    state: BundleDefinition = emptyState(),
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(qualifier, state, clazz, parameters) }
}

inline fun <reified T : ViewModel> Fragment.getStateViewModel(
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition = emptyState(),
    noinline parameters: ParametersDefinition? = null,
): T {
    return getStateViewModel(qualifier, state, T::class, parameters)
}

@OptIn(KoinInternalApi::class)
fun <T : ViewModel> Fragment.getStateViewModel(
    qualifier: Qualifier? = null,
    state: BundleDefinition = emptyState(),
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null,
): T {
    return getKoinScope().getViewModel(qualifier, { from(activity as ViewModelStoreOwner, this) }, clazz, state, parameters)
}