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
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.module.Module


/**
 * ViewModel DSL Extension
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param name - definition name
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> Module.viewModel(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
) {
    factory(name, override, definition).setIsViewModel()
}

const val VIEW_MODEL_KEY = "isViewModel"

fun BeanDefinition<*>.setIsViewModel() {
    attributes[VIEW_MODEL_KEY] = true
}

fun BeanDefinition<*>.isViewModel(): Boolean {
    return attributes[VIEW_MODEL_KEY] ?: false
}