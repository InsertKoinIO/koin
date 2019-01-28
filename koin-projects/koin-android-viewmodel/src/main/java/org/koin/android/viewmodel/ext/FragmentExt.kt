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
package org.koin.android.viewmodel.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStoreOwner
import android.support.v4.app.Fragment
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.android.viewmodel.ViewModelStoreOwnerDefinition
import org.koin.android.viewmodel.resolveViewModelInstance
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition

/**
 * Fragment extensiosn to help for Viewmodel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy getByClass a viewModel instance shared with Activity
 *
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: "parentFragment", "activity". Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 * @param koin - Custom koin for context isolation
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    name: String? = null,
    noinline from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
    noinline parameters: ParametersDefinition? = null,
    koin: Koin = GlobalContext.get().koin
) = lazy { getSharedViewModel<T>(name, from, parameters, koin) }

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: ("parentFragment", "activity"). Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 * @param koin - Custom koin for context isolation
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    name: String? = null,
    noinline from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
    noinline parameters: ParametersDefinition? = null,
    koin: Koin = GlobalContext.get().koin
) = resolveViewModelInstance(
    ViewModelParameters(
        T::class,
        name,
        from,
        parameters
    ), koin
)
