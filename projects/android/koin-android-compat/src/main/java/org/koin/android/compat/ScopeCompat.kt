/*
 * Copyright 2017-present the original author or authors.
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
package org.koin.android.compat

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Scope functions to help for ViewModel in Java
 *
 * @author Jeziel Lago
 */
object ScopeCompat {

    //TODO Deprecate and add new

    /**
     * Lazy get a viewModel instance
     *
     * @param scope
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     * @param clazz
     */
    @JvmOverloads
    @JvmStatic
    @MainThread
    fun <T : ViewModel> viewModel(
        scope: Scope,
        owner: ViewModelStoreOwner,
        clazz: Class<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) { getViewModel(scope, owner, clazz, qualifier, parameters) }
    }


    /**
     * Get a viewModel instance
     *
     * @param clazz - Class of the BeanDefinition to retrieve
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     */
    @OptIn(KoinInternalApi::class)
    @JvmOverloads
    @JvmStatic
    @MainThread
    fun <T : ViewModel> getViewModel(
        scope: Scope,
        owner: ViewModelStoreOwner,
        clazz: Class<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): T {
        return resolveViewModelCompat(
            clazz,
            owner.viewModelStore,
            extras = CreationExtras.Empty,
            qualifier = qualifier,
            parameters = parameters,
            scope = scope
        )
    }
}