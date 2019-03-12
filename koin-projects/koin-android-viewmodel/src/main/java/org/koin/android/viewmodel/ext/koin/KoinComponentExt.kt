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
@file:Suppress("unused")

package org.koin.android.viewmodel.ext.koin

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.android.viewmodel.getViewModel
import org.koin.core.KoinComponent

/**
 * Lazy getByClass a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> KoinComponent.viewModel(
        lifecycleOwner: LifecycleOwner,
        parameters: ViewModelParameters<T>
): Lazy<T> = lazy { getViewModel(lifecycleOwner, parameters) }

/**
 * Get a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> KoinComponent.getViewModel(
        lifecycleOwner: LifecycleOwner,
        parameters: ViewModelParameters<T>
): T = getKoin().getViewModel(parameters)