/*
 * Copyright 2017-2023 the original author or authors.
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
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Fragment extension to help for Viewmodel
 *
 * @author Arnaud Giuliani
 */
//TODO To be removed in 4.1
@Deprecated("This API is deprecated and will be removed in next version. Use Fragment.activityViewModel() with extras: CreationExtras", level = DeprecationLevel.ERROR)
@MainThread
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    qualifier: Qualifier? = null,
    noinline owner: () -> ViewModelStoreOwner = { requireActivity() },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> = TODO("this function is deprecated")

@Deprecated("This API is deprecated and will be removed in next version. Use Fragment.getActivityViewModel() with extras: CreationExtras", level = DeprecationLevel.ERROR)
@OptIn(KoinInternalApi::class)
@MainThread
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    qualifier: Qualifier? = null,
    noinline owner: () -> ViewModelStoreOwner = { requireActivity() },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: ParametersDefinition? = null,
): T = TODO("this function is deprecated")