/*
 * Copyright 2017-Present the original author or authors.
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
@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.koin.viewmodel.scope

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.option.hasViewModelScopeFactory
import org.koin.core.scope.Scope
import org.koin.viewmodel.factory.ViewModelScopeAutoCloseable

/**
 * Class to help support Koin Scope in a ViewModel
 * create directly a scope instance for current ViewModel
 *
 * allow to intercept before scope closing with `onCloseScope`, to be overriden
 *
 * Destroy linked scope with ViewModelScopeAutoCloseable
 *
 * @see ViewModelScopeAutoCloseable
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
abstract class ScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = viewModelScope()

    /**
     * To override to add behavior before closing Scope
     */
    @Deprecated("Not used anymore. Now close scope automatically with ViewModelScopeAutoCloseable")
    open fun onCloseScope(){}
}

/**
 * Create a ViewModel Scope as ViewModelScope archetype for given ViewModel
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
fun KoinScopeComponent.viewModelScope() : Scope {
    if (this !is ViewModel) {
        error("$this should implement ViewModel() class")
    }
    val koin = getKoin()
    if (koin.optionRegistry.hasViewModelScopeFactory()){
        koin.logger.warn("${this::class} is using viewModelScope() while you are using viewModelScopeFactory() option. Remove viewModelScope() usage to use ViewModel constructor injection with automatic scope creation.")
    }
    val vmScope = createScope(source = this, scopeArchetype = ViewModelScopeArchetype)
    addCloseable(ViewModelScopeAutoCloseable(vmScope.id,vmScope.getKoin()))
    return vmScope
}