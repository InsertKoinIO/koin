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

import androidx.lifecycle.ViewModel
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition


/**
 * ViewModel DSL Extension
 * Allow to declare a vieModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param name - definition name
 * @param isSingleton - is single or factory
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> ModuleDefinition.viewModel(
    name: String = "",
    override: Boolean = false,
    noinline definition: Definition<T>
) {
    val bean = factory(name, override, definition)
    bean.bind(ViewModel::class)
}

/**
 * ViewModel DSL Extension
 * Allow to build a vieModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param name - definition name
 * @param isSingleton - is single or factory
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> ModuleDefinition.viewModel(
    name: String = "",
    override: Boolean = false
) {
    val bean = factory(name, override) { build<T>() }
    bean.bind(ViewModel::class)
}