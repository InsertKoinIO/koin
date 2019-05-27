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
package org.koin.android.viewmodel.dsl

import android.arch.lifecycle.ViewModel
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeSet

/**
 * ViewModel DSL Extension
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param qualifier - definition qualifier
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> ScopeSet.viewModel(
        qualifier: Qualifier? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createFactory(qualifier, this.qualifier, definition)
    declareDefinition(beanDefinition, Options(false, override))
    beanDefinition.setIsViewModel()
    if (!definitions.contains(beanDefinition)) {
        definitions.add(beanDefinition)
    } else {
        throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
    }
    return beanDefinition
}