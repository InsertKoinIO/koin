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
package org.koin.android.viewmodel.ext.android

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStoreOwner
import org.koin.android.viewmodel.ViewModelOwner.Companion.from
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * ViewModelStoreOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy {
        getViewModel<T>(qualifier, parameters)
    }
}

fun <T : ViewModel> ViewModelStoreOwner.viewModel(
    qualifier: Qualifier? = null,
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { getViewModel(qualifier, clazz, parameters) }
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(qualifier, T::class, parameters)
}

fun <T : ViewModel> ViewModelStoreOwner.getViewModel(
    qualifier: Qualifier? = null,
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): T {
    return GlobalContext.get().getViewModel(qualifier, { from(this) }, clazz, parameters)
}