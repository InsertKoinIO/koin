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
package org.koin.android.viewmodel.koin

import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelOwnerDefinition
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.android.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternal
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 *
 */
inline fun <reified T : ViewModel> Koin.viewModel(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        noinline owner: ViewModelOwnerDefinition,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(mode) { getViewModel<T>(qualifier, owner, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
        qualifier: Qualifier? = null,
        noinline owner: ViewModelOwnerDefinition,
        noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(qualifier, owner, T::class, parameters)
}

@OptIn(KoinInternal::class)
fun <T : ViewModel> Koin.getViewModel(
        qualifier: Qualifier? = null,
        owner: ViewModelOwnerDefinition,
        clazz: KClass<T>,
        parameters: ParametersDefinition? = null
): T {
    return scopeRegistry.rootScope.getViewModel(qualifier, owner, clazz, parameters)
}

@OptIn(KoinInternal::class)
fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return scopeRegistry.rootScope.getViewModel(viewModelParameters)
}