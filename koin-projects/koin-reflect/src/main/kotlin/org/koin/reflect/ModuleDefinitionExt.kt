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
package org.koin.reflect

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.BeanDefinition

/**
 * Build instance with Koin injected dependencies
 */
inline fun <reified T : Any> ModuleDefinition.build(): T {
//    lateinit var instance: T
    val clazz = T::class.java

//    val duration = measureDuration {
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { get(clazz = it) }.toTypedArray()
    return ctor.newInstance(*args) as T
//    }

//    Koin.logger.debug("[$clazz] built in $duration ms")
//    return instance
}

inline fun <reified T : Any> ModuleDefinition.single(
    name: String = "",
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<T> {
    return single(name, createOnStart, override) { build<T>() }
}

inline fun <reified T : Any, reified R : Any> ModuleDefinition.singleOf(
    name: String = "",
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<*> {
    return single(name, createOnStart, override) { build<T>() as R }
}

inline fun <reified T : Any> ModuleDefinition.factory(
    name: String = "",
    override: Boolean = false
): BeanDefinition<T> {
    return factory(name, override) { build<T>() }
}

inline fun <reified T : Any, reified R : Any> ModuleDefinition.factoryOf(
    name: String = "",
    override: Boolean = false
): BeanDefinition<*> {
    return factory(name, override) { build<T>() as R }
}