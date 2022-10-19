/*
 * Copyright 2017-2021 the original author or authors.
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

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.android.ext.android.getKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier

/**
 * ViewModelStoreOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

@MainThread
inline fun <reified T : ViewModel> Fragment.activityViewModel(
    qualifier: Qualifier? = null,
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<T> {
    return lazy {
        getActivityViewModel(qualifier, extrasProducer, parameters)
    }
}

@OptIn(KoinInternalApi::class)
@MainThread
inline fun <reified T : ViewModel> Fragment.getActivityViewModel(
    qualifier: Qualifier? = null,
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): T {
    return getViewModel(
        T::class,
        requireActivity().viewModelStore,
        extras = extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras,
        qualifier = qualifier,
        parameters = parameters,
        scope = getKoinScope()
    )
}