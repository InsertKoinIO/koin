/*
 * Copyright 2017-2020 the original author or authors.
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
package org.koin.android.viewmodel.compat

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * LifecycleOwner functions to help for ViewModel in Java
 *
 * @author Jeziel Lago
 */
object ViewModelCompat {

    /**
     * Lazy get a viewModel instance
     *
     * @param owner - LifecycleOwner
     * @param clazz - viewModel class dependency
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     */

    @JvmStatic
    fun <T : ViewModel> viewModel(
            owner: LifecycleOwner,
            clazz: Class<T>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null
    ): Lazy<T> = owner.viewModel(clazz.kotlin, qualifier, parameters)


    /**
     * Get a viewModel instance
     *
     * @param owner - LifecycleOwner
     * @param clazz - Class of the BeanDefinition to retrieve
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     */

    @JvmStatic
    fun <T : ViewModel> getViewModel(
            owner: LifecycleOwner,
            clazz: Class<T>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null
    ): T {
        return owner.getViewModel(clazz.kotlin, qualifier, parameters)
    }
}