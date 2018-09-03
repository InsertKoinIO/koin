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
import org.koin.dsl.definition.Kind

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
    return provide(name, createOnStart, override, Kind.Single) { create<T>() }
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
    return provide(name, false, override, Kind.Factory) { create<T>() }
}

/**
 * Create a Scope definition for given type T
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any> ModuleDefinition.scope(
    name: String = "",
    override: Boolean = false
): BeanDefinition<T> {
    return provide(name, false, override, Kind.Scope) { create<T>() }
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
 * Create a Single definition for given type T to build and cast as R
 * @param name
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified T : Any, reified R : Any> ModuleDefinition.singleBy(
    name: String = "",
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<R> {
    return provide(name, createOnStart, override, Kind.Single) { create<T>() as R }
}

/**
 * Create a Factory definition for given type T to build and cast as R
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any, reified R : Any> ModuleDefinition.factoryBy(
    name: String = "",
    override: Boolean = false
): BeanDefinition<R> {
    return provide(name, false, override, Kind.Factory) { create<T>() as R }
}

/**
 * Create a Scope definition for given type T, applied to R
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any, reified R : Any> ModuleDefinition.scopeBy(
    name: String = "",
    override: Boolean = false
): BeanDefinition<R> {
    return provide(name, false, override, Kind.Scope) { create<T>() as R }
}

/**
 * Resolve a component from its class
 *
 * @param name
 * @param clazz - java class
 * @param parameters
 */
fun <T : Any> ModuleDefinition.getForClass(
    name: String = "",
    clazz: Class<T>,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T = koinContext.get(name, clazz = clazz.kotlin, parameters = parameters)