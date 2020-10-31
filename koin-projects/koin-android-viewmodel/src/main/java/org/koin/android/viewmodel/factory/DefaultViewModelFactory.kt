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
package org.koin.android.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.core.scope.Scope

/**
 * Default ViewModel Factory
 *
 * ViewModelProvider.Factory that resolves Koin instance
 */
class DefaultViewModelFactory<T : ViewModel>(val scope: Scope,
    val parameters: ViewModelParameter<T>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return scope.get(parameters.clazz, parameters.qualifier, parameters.parameters) as T
    }
}