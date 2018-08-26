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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStoreOwner
import android.support.v4.app.Fragment
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition

/**
 * Fragment extensiosn to help for Viewmodel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy getByClass a viewModel instance shared with Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: "parentFragment", "activity". Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline from: ViewModelStoreOwnerDefinition = { activity!! as ViewModelStoreOwner },
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = viewModelByClass(T::class, key, name, module, from, parameters)

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: ("parentFragment", "activity"). Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline from: ViewModelStoreOwnerDefinition = {activity!! as ViewModelStoreOwner},
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = getViewModelByClass(T::class, key, name, module, from, parameters)
