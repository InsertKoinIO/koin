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
package org.koin.androidx.viewmodel.ext.koin

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.ViewModelStoreOwnerDefinition
import org.koin.androidx.viewmodel.ext.android.getViewModelByClass
import org.koin.androidx.viewmodel.ext.android.viewModelByClass
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent

/**
 * ViewModel request options
 */
data class ViewModelOptions(
    val key: String? = null,
    val name: String? = null,
    val from: ViewModelStoreOwnerDefinition? = null,
    val parameters: ParameterDefinition = emptyParameterDefinition()
)

/**
 * Lazy getByClass a viewModel instance
 *
 * @param lifecycleOwner
 * @param options
 */
inline fun <reified T : ViewModel> KoinComponent.viewModel(
    lifecycleOwner: LifecycleOwner,
    options: ViewModelOptions = ViewModelOptions()
) = lifecycleOwner.viewModelByClass(
    T::class,
    options.key,
    options.name,
    options.from,
    options.parameters
)

/**
 * Get a viewModel instance
 *
 * @param lifecycleOwner
 * @param options
 */
inline fun <reified T : ViewModel> KoinComponent.getViewModel(
    lifecycleOwner: LifecycleOwner,
    options: ViewModelOptions = ViewModelOptions()
) = lifecycleOwner.getViewModelByClass(
    T::class,
    options.key,
    options.name,
    options.from,
    options.parameters
)