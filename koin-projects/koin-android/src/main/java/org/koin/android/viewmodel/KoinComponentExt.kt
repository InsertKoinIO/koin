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

package org.koin.android.viewmodel

import org.koin.core.CustomRequest
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.getWith

/**
 * Create instance with parameters for ViewModelFactory
 * @see implemented ViewModelFactory
 *
 * @param p - ViewModelParameters
 * @param clazz - class
 */
fun <T> KoinComponent.createInstance(p: ViewModelParameters, clazz: Class<T>): T {
    // Get params to pass to factory
    val name = p.name
    val module = p.module
    val params = p.parameters
    // Clear local stuff

    return if (name != null) {
        retrieveViewModel(clazz, name, module, params)
    } else retrieveViewModel(clazz, module, params)
}

/**
 * Retrieve ViewModel Instance from class/name
 */
internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    name: String,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(filterIsViewModel).filter { registry.filterByNameAndClass(it, name, clazz) }
}, module, clazz, params))

/**
 * Retrieve ViewModel Instance from class
 */
internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(filterIsViewModel).filter { registry.filterByClass(it, clazz) }
}, module, clazz, params))

// reusable filter
internal val filterIsViewModel = { def: BeanDefinition<*> -> def.isViewModel() }