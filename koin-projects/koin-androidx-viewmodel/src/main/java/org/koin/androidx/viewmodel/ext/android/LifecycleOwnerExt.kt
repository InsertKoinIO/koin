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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import org.koin.core.Koin
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy getByClass a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = viewModelByClass(T::class, key, name, null, parameters)

/**
 * Lazy getByClass a viewModel instance
 *
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. null to assume "this" as the ViewModelStoreOwner
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.viewModelByClass(
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    from: ViewModelStoreOwnerDefinition? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { getViewModelByClass(clazz, key, name, from, parameters) }

/**
 * Get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = getViewModelByClass(T::class, key, name, null, parameters)

fun <T : ViewModel> LifecycleOwner.getViewModelByClass(
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    from: ViewModelStoreOwnerDefinition? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T {
    Koin.logger.debug("[ViewModel] ~ '$clazz'(name:'$name' key:'$key') - $this")

    val vmStore: ViewModelStore = getViewModelStore(from, clazz)

    val viewModelProvider =
        makeViewModelProvider(vmStore, name, clazz, parameters)

    return viewModelProvider.getInstance(key, clazz)
}

private fun <T : ViewModel> ViewModelProvider.getInstance(
    key: String?,
    clazz: KClass<T>
): T {
    return if (key != null) {
        this.get(key, clazz.java)
    } else {
        this.get(clazz.java)
    }
}

private fun <T : ViewModel> LifecycleOwner.getViewModelStore(
    from: ViewModelStoreOwnerDefinition?,
    clazz: KClass<T>
): ViewModelStore {
    return when {
        from != null -> from().viewModelStore
        this is ViewModelStoreOwner -> this.viewModelStore
        else -> error("Can't getByClass ViewModel '$clazz' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
    }
}

private fun <T : ViewModel> makeViewModelProvider(
    vmStore: ViewModelStore,
    name: String?,
    clazz: KClass<T>,
    parameters: ParameterDefinition
): ViewModelProvider {
    return ViewModelProvider(
        vmStore,
        object : ViewModelProvider.Factory, KoinComponent {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return get(name ?: "", clazz, parameters = parameters)
            }
        })
}

/**
 * Function to define a ViewModelStoreOwner
 */
typealias ViewModelStoreOwnerDefinition = () -> ViewModelStoreOwner
