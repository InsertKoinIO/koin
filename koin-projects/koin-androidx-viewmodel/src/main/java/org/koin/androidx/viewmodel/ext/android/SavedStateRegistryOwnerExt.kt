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
package org.koin.androidx.viewmodel.ext.android

import android.content.ComponentCallbacks
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getStateViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

fun <T : ViewModel> SavedStateRegistryOwner.stateViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(clazz, qualifier, bundle, parameters) }
}

inline fun <reified T : ViewModel> SavedStateRegistryOwner.stateViewModel(
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(T::class, qualifier, bundle, parameters) }
}

inline fun <reified T : ViewModel> SavedStateRegistryOwner.getStateViewModel(
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getStateViewModel(T::class, qualifier, bundle, parameters)
}

fun <T : ViewModel> SavedStateRegistryOwner.getStateViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): T {
    val bundleOrDefault: Bundle = bundle ?: Bundle()
    return getKoin().getStateViewModel(this, clazz, qualifier, bundleOrDefault, parameters)
}

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()