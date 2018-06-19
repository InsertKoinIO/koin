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

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.NoBeanDefFoundException

/**
 * KoinContext Extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Retrieve an instance by its class canonical name
 */
fun <T> KoinContext.getByTypeName(canonicalName: String, module: String? = null, parameters: ParameterDefinition): T {
    val foundDefinitions =
        beanRegistry.definitions.filter { it.clazz.java.canonicalName == canonicalName }.distinct()
    return getWithDefinitions(foundDefinitions, module, parameters, "for class name '$canonicalName'")
}

/**
 * Retrieve an instance by its bean beanDefinition name
 */
fun <T> KoinContext.getByName(name: String, module: String? = null, parameters: ParameterDefinition): T {
    val foundDefinitions = beanRegistry.definitions.filter { it.name == name }.distinct()
    return getWithDefinitions(foundDefinitions, module, parameters, "for bean name '$name'")
}

/**
 * Retrieve bean beanDefinition instance from given definitions
 */
private fun <T> KoinContext.getWithDefinitions(
    foundDefinitions: List<BeanDefinition<*>>,
    module: String? = null,
    parameters: ParameterDefinition,
    message: String
): T {
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean beanDefinition found $message")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstance(module, def.clazz, parameters, { listOf(def) })
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found $message")
    }
}