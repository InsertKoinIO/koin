/*
 * Copyright 2017-2018 the original author or authors.
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import org.koin.androidx.viewmodel.*
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Scope extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy get a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 * @param parameters - parameters to pass to the BeanDefinition
 * @param clazz
 */
fun <T : ViewModel> Scope.viewModel(
    storeOwner: ViewModelStoreDefinition,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null,
    defaultArguments: ViewModelState? = null
): Lazy<T> = lazy { getViewModel(storeOwner, clazz, qualifier, defaultArguments, parameters) }

/**
 * Lazy getByClass a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Scope.viewModel(
    noinline storeOwner: ViewModelStoreDefinition,
    qualifier: Qualifier? = null,
    noinline defaultArguments: ViewModelState? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModel<T>(storeOwner, qualifier, defaultArguments, parameters) }

/**
 * Lazy getByClass a viewModel instance
 *
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Scope.getViewModel(
    storeOwner: ViewModelStoreDefinition,
    qualifier: Qualifier? = null,
    noinline defaultArguments: ViewModelState? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(
        ViewModelParameters(
            T::class,
            storeOwner(),
            qualifier,
            parameters,
            defaultArguments
        )
    )
}

/**
 * Lazy getByClass a viewModel instance
 *
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> Scope.getViewModel(
    storeOwner: ViewModelStoreDefinition,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    defaultArguments: ViewModelState? = null,
    parameters: ParametersDefinition? = null
): T {
    return getViewModel(
        ViewModelParameters(
            clazz,
            storeOwner(),
            qualifier,
            parameters,
            defaultArguments
        )
    )
}

/**
 * resolve instance
 * @param viewModelParameters
 */
fun <T : ViewModel> Scope.getViewModel(viewModelParameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = viewModelParameters.store
    val viewModelProvider = createViewModelProvider(vmStore, viewModelParameters)
    return viewModelProvider.resolveInstance(viewModelParameters)
}