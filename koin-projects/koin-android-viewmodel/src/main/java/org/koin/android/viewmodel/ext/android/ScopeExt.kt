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
package org.koin.android.viewmodel.ext.android

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.android.viewmodel.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy get a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 * @param clazz
 */
fun <T : ViewModel> Scope.viewModel(
        owner: LifecycleOwner,
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModel(owner, clazz, qualifier, parameters) }

/**
 * Lazy getByClass a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Scope.viewModel(
        owner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModel<T>(owner, qualifier, parameters) }

/**
 * Get a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Scope.getViewModel(
        owner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(owner, T::class, qualifier, parameters)
}

/**
 * Lazy getByClass a viewModel instance
 *
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> Scope.getViewModel(
        owner: LifecycleOwner,
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
    return getViewModel(
            ViewModelParameters(
                    clazz,
                    owner,
                    qualifier,
                    parameters = parameters
            )
    )
}
