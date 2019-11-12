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
package org.koin.androidx.viewmodel.ext.android

import android.content.ComponentCallbacks
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.ViewModelState
import org.koin.androidx.viewmodel.ViewModelStoreDefinition
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
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
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 * @param parameters - parameters to pass to the BeanDefinition
 * @param clazz
 */
fun <T : ViewModel> LifecycleOwner.viewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    storeDefinition: ViewModelStoreDefinition = { getViewModelStore() },
    defaultArguments: ViewModelState? = null,
    parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModel(clazz, qualifier, storeDefinition, defaultArguments, parameters) }

/**
 * Lazy getByClass a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
    qualifier: Qualifier? = null,
    noinline storeDefinition: ViewModelStoreDefinition = { getViewModelStore() },
    noinline defaultArguments: ViewModelState? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModel<T>(qualifier, storeDefinition, defaultArguments, parameters) }

/**
 * Get a viewModel instance
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
    qualifier: Qualifier? = null,
    noinline storeDefinition: ViewModelStoreDefinition = { getViewModelStore() },
    noinline defaultArguments: ViewModelState? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(
        T::class, qualifier, storeDefinition, defaultArguments, parameters
    )
}

/**
 * Lazy getByClass a viewModel instance
 *
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 * @param defaultArguments - Default arguments for SavedStateHandle if useState = true
 */
fun <T : ViewModel> LifecycleOwner.getViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    storeDefinition: ViewModelStoreDefinition = { getViewModelStore() },
    defaultArguments: ViewModelState? = null,
    parameters: ParametersDefinition? = null
): T {
    return getKoin().getViewModel(
        ViewModelParameters(clazz, storeDefinition(), qualifier, parameters, defaultArguments)
    )
}

fun LifecycleOwner.getViewModelStore(): ViewModelStore {
    return when (this) {
        is FragmentActivity -> this.viewModelStore
        is Fragment -> this.viewModelStore
        else -> error("LifecycleOwner is not either FragmentActivity nor Fragment")
    }
}

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()