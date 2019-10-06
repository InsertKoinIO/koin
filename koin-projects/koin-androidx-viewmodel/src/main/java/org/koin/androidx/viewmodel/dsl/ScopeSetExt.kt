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
package org.koin.androidx.viewmodel.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Options
import org.koin.core.definition.createFactory
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet

/**
 * ViewModel DSL Extension
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param qualifier - definition qualifier
 * @param useState - whether this view model wants a SavedStateHandle definition parameter
 * @param override - allow definition override
 */
inline fun <S : Scope, reified T : ViewModel> ScopeSet<S>.viewModel(
        qualifier: Qualifier? = null,
        override: Boolean = false,
        useState: Boolean = false,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    val beanDefinition = this.createFactory(qualifier, this.qualifier, definition)
    declareDefinition(beanDefinition, Options(false, override))
    beanDefinition.setIsViewModel()
    if (useState) {
        beanDefinition.setIsStateViewModel()
    }
    if (!definitions.contains(beanDefinition)) {
        definitions.add(beanDefinition)
    } else {
        throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
    }
    return beanDefinition
}