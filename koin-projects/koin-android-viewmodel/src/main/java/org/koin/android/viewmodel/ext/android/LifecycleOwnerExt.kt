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
package org.koin.android.viewmodel.ext.android

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStore
import android.content.ComponentCallbacks
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

fun <T : ViewModel> LifecycleOwner.viewModel(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) { getViewModel(clazz, qualifier, parameters) }
}

inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) { getViewModel(T::class, qualifier, parameters) }
}

inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T {
        return getViewModel(T::class, qualifier, parameters)
}

fun <T : ViewModel> LifecycleOwner.getViewModel(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
        return getKoin().getViewModel(this, clazz, qualifier, parameters)
}

fun LifecycleOwner.getViewModelStore(): ViewModelStore {
        return when (this) {
                is FragmentActivity -> this.viewModelStore
                is Fragment -> this.viewModelStore
                else -> error("LifecycleOwner is not either FragmentActivity nor Fragment")
        }
}

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()
