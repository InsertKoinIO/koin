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

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * LifecycleOwner functions to help for ViewModel in Java
 *
 * @author Jeziel Lago
 */
object ViewModelCompat {

    //TODO Deprecate and add new API against Activity/Fragment

    /**
     * Lazy get a viewModel instance
     *
     * @param owner - ViewModelStoreOwner
     * @param clazz - viewModel class dependency
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     */
    @JvmOverloads
    @JvmStatic
    @MainThread
    fun <T : ViewModel> viewModel(
        owner: ViewModelStoreOwner,
        clazz: Class<T>,
        qualifier: Qualifier? = null,
        extrasProducer: (() -> CreationExtras)? = null,
        parameters: ParametersDefinition? = null
    ): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
            getViewModel(owner, clazz, qualifier, extrasProducer, parameters)
        }
    }


    /**
     * Get a viewModel instance
     *
     * @param owner - ViewModelStoreOwner
     * @param clazz - Class of the BeanDefinition to retrieve
     * @param qualifier - Koin BeanDefinition qualifier (if have several ViewModel beanDefinition of the same type)
     * @param parameters - parameters to pass to the BeanDefinition
     */
    @OptIn(KoinInternalApi::class)
    @JvmOverloads
    @JvmStatic
    @MainThread
    fun <T : ViewModel> getViewModel(
        owner: ViewModelStoreOwner,
        clazz: Class<T>,
        qualifier: Qualifier? = null,
        extrasProducer: (() -> CreationExtras)? = null,
        parameters: ParametersDefinition? = null
    ): T = resolveViewModelCompat(
        clazz,
        owner.viewModelStore,
        extras = extrasProducer?.invoke() ?: getDefaultViewModelCreationExtras(owner),
        qualifier = qualifier,
        parameters = parameters,
        scope = GlobalContext.get().scopeRegistry.rootScope
    )

    private fun getDefaultViewModelCreationExtras(owner: ViewModelStoreOwner): CreationExtras =
        if (owner is ComponentActivity) owner.defaultViewModelCreationExtras else CreationExtras.Empty
}