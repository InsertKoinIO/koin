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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.ViewModelStoreOwnerDefinition
import org.koin.androidx.viewmodel.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Fragment extensiosn to help for Viewmodel
 *
 * @author Arnaud Giuliani
 */

/**
 * Lazy getByClass a viewModel instance shared with Activity
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: "parentFragment", "activity". Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
        qualifier: Qualifier? = null,
        noinline from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
        noinline parameters: ParametersDefinition? = null
): Lazy<T> = kotlin.lazy { getSharedViewModel<T>(qualifier, from, parameters) }

/**
 * Lazy getByClass a viewModel instance shared with Activity
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: "parentFragment", "activity". Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 * @param clazz
 */
fun <T : ViewModel> Fragment.sharedViewModel(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
        parameters: ParametersDefinition? = null
): Lazy<T> = kotlin.lazy { getSharedViewModel(clazz, qualifier, from, parameters) }

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: ("parentFragment", "activity"). Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
        qualifier: Qualifier? = null,
        noinline from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
        noinline parameters: ParametersDefinition? = null
): T {
    return getSharedViewModel(T::class, qualifier, from, parameters)
}

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
 * @param from - ViewModelStoreOwner that will store the viewModel instance. Examples: ("parentFragment", "activity"). Default: "activity"
 * @param parameters - parameters to pass to the BeanDefinition
 * @param clazz
 */
fun <T : ViewModel> Fragment.getSharedViewModel(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        from: ViewModelStoreOwnerDefinition = { activity as ViewModelStoreOwner },
        parameters: ParametersDefinition? = null
): T {
    return getKoin().getViewModel(
            ViewModelParameters(
                    clazz,
                    this@getSharedViewModel,
                    qualifier,
                    from,
                    parameters
            )
    )
}