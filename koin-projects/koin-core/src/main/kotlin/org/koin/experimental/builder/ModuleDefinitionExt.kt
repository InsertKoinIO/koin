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
package org.koin.experimental.builder

import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.BeanDefinition

/**
 * Create a Single definition for given type T
 * @param name
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified T : Any> ModuleDefinition.single(
    name: String = "",
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<T> {
    return provide(name, createOnStart, override) { create<T>() }
}

/**
 * Create a Factory definition for given type T
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any> ModuleDefinition.factory(
    name: String = "",
    override: Boolean = false
): BeanDefinition<T> {
    return provide(name, false, override, false) { create<T>() }
}

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> ModuleDefinition.create(): T {
    val clazz = T::class.java
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { getForClass(clazz = it) }.toTypedArray()
    return ctor.newInstance(*args) as T
}

/**
 * Resolve a component from its class
 *
 * @param name
 * @param clazz - java class
 * @param parameters
 */
fun <T> ModuleDefinition.getForClass(
    name: String = "",
    clazz: Class<T>,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T = koinContext.getForClass(name, clazz.canonicalName, parameters = parameters)