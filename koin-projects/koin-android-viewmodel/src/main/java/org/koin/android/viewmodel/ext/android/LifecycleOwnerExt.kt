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
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.android.viewmodel.ViewModelFactory
import org.koin.core.Koin
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
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
    ViewModelFactory.apply {
        _parameters = parameters
        _name = name
        _module = module
    }
    val clazzName = clazz.java.simpleName
    val viewModelProvider = when {
        this is FragmentActivity -> {
            Koin.logger.log("[ViewModel] get '$clazzName' for FragmentActivity @ $this")
            ViewModelProvider(ViewModelStores.of(this), ViewModelFactory)
        }
        this is Fragment -> {
            if (fromActivity) {
                Koin.logger.log("[ViewModel] get '$clazzName' for FragmentActivity @ ${this.activity}")
                ViewModelProvider(ViewModelStores.of(this.activity), ViewModelFactory)
            } else {
                Koin.logger.log("[ViewModel] get '$clazzName' for Fragment @ $this")
                ViewModelProvider(ViewModelStores.of(this), ViewModelFactory)
            }
        }
        else -> error("Can't get ViewModel '$clazzName' on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(
        key,
        clazz.java
    ) else viewModelProvider.get(clazz.java)
}
