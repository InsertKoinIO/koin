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
@file:Suppress("DeprecatedCallableAddReplaceWith")

package org.koin.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.core.annotation.KoinInternalApi

/**
 * Resolve ViewModel instance
 *
 * @param qualifier
 * @param parameters
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
fun defaultNavExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras = when {
    //TODO To be fully verified
    viewModelStoreOwner is NavBackStackEntry && viewModelStoreOwner.arguments != null -> viewModelStoreOwner.arguments?.toExtras(viewModelStoreOwner) ?: CreationExtras.Empty
    viewModelStoreOwner is HasDefaultViewModelProviderFactory -> viewModelStoreOwner.defaultViewModelCreationExtras
    else -> CreationExtras.Empty
}

/**
 * Convert current Bundle to CreationExtras
 * @param viewModelStoreOwner
 */
@KoinInternalApi
fun Bundle.toExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras? {
    return if (keySet().isEmpty()) null
    else {
        runCatching {
            MutableCreationExtras().also { extras ->
                extras[DEFAULT_ARGS_KEY] = this
                extras[VIEW_MODEL_STORE_OWNER_KEY] = viewModelStoreOwner
                extras[SAVED_STATE_REGISTRY_OWNER_KEY] = viewModelStoreOwner as SavedStateRegistryOwner
            }
        }.getOrNull()
    }
}