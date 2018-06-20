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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ViewModelFactory
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.core.Koin
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import kotlin.reflect.KClass

/**
 * LifecycleOwner ViewModel extensions
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = viewModelByClass(false, T::class, key, name, module, parameters)

/**
 * Lazy get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default true)
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.viewModelByClass(
    fromActivity: Boolean,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { getViewModelByClass(fromActivity, clazz, key, name, module, parameters) }

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
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = getViewModelByClass(false, T::class, key, name, module, parameters)

/**
 * Get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default false) - not used if on Activity
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.getViewModelByClass(
    fromActivity: Boolean = false,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T {

    ViewModelFactory.viewModelParameters = ViewModelParameters(name,module,parameters)
    val clazzName = clazz.java.simpleName
    val viewModelProvider = when {
        this is FragmentActivity -> {
            Koin.logger.log("[ViewModel] get '$clazzName' for FragmentActivity @ $this")
            ViewModelProvider(this.viewModelStore, ViewModelFactory)
        }
        this is Fragment -> {
            if (fromActivity) {
                Koin.logger.log("[ViewModel] get '$clazzName' for FragmentActivity @ ${this.activity}")
                ViewModelProvider(
                    this.activity?.viewModelStore ?: error("Can't create ViewModel instance - Parent activity is null"),
                    ViewModelFactory
                )
            } else {
                Koin.logger.log("[ViewModel] get '$clazzName' for Fragment @ $this")
                ViewModelProvider(this.viewModelStore, ViewModelFactory)
            }
        }
        else -> error("Can't get ViewModel on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(
        key,
        clazz.java
    ) else viewModelProvider.get(clazz.java)
}
