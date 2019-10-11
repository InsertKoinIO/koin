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
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.RootScope

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
inline fun <reified T : ViewModel> Module.viewModel(
        qualifier: Qualifier? = null,
        override: Boolean = false,
        useState: Boolean = false,
        noinline definition: Definition<RootScope, T>
): BeanDefinition<T> {
    val beanDefinition = factory(qualifier, override, definition)
    beanDefinition.setIsViewModel()
    if(useState) {
        beanDefinition.setIsStateViewModel()
    }
    return beanDefinition
}

const val ATTRIBUTE_VIEW_MODEL = "isViewModel"
const val ATTRIBUTE_VIEW_MODEL_SAVED_STATE = "isSavedStateViewModel"

fun BeanDefinition<*>.setIsViewModel() {
    properties[ATTRIBUTE_VIEW_MODEL] = true
}

fun BeanDefinition<*>.isViewModel(): Boolean {
    return properties.getOrNull(ATTRIBUTE_VIEW_MODEL) ?: false
}

/**
 * StateViewModel DSL Extension
 * Allow to declare a stateful ViewModel - will have a SavedStateHandle passed in constructor
 *
 * @author Marek Kedzierski
 */
fun BeanDefinition<*>.setIsStateViewModel() {
    properties[ATTRIBUTE_VIEW_MODEL_SAVED_STATE] = true
}

fun BeanDefinition<*>.isStateViewModel(): Boolean {
    return properties.getOrNull(ATTRIBUTE_VIEW_MODEL_SAVED_STATE) ?: false
}