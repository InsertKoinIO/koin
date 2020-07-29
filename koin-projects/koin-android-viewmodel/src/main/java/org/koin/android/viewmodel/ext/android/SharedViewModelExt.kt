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

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ViewModelOwner.Companion.from
import org.koin.android.viewmodel.ViewModelOwnerDefinition
import org.koin.android.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * LifecycleOwner extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelOwnerDefinition = { from(requireActivity()) },
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel<T>(qualifier, owner, parameters)
    }
}

fun <T : ViewModel> Fragment.sharedViewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelOwnerDefinition = { from(requireActivity()) },
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel(qualifier, owner, clazz, parameters) }
}

inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelOwnerDefinition = { from(requireActivity()) },
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(qualifier, owner, parameters)
}

fun <T : ViewModel> Fragment.getSharedViewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelOwnerDefinition = { from(requireActivity()) },
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): T {
    return getKoin().getViewModel(qualifier, owner, clazz, parameters)
}