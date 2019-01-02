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

import org.koin.core.bean.BeanDefinition
import org.koin.core.module.Module

/**
 * Create a Single definition for given type T
 * @param name
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<T> {
    return single(name, createOnStart, override) { create<T>() }
}

/**
 * Create a Factory definition for given type T
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    override: Boolean = false
): BeanDefinition<T> {
    return factory(name, override) { create<T>() }
}

///**
// * Create a Scope definition for given type T
// *
// * @param scopeId
// * @param name
// * @param override - allow definition override
// */
//inline fun <reified T : Any> Module.scoped(
//    name: String? = null,
//    override: Boolean = false
//): BeanDefinition<T> {
//    return scoped(name, override) { create<T>(getCurrentScope()) }
//}

/**
 * Create a Single definition for given type T to modules and cast as R
 * @param name
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> Module.singleBy(
    name: String? = null,
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<R> {
    return single(name, createOnStart, override) { create<T>() as R }
}

/**
 * Create a Factory definition for given type T to modules and cast as R
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> Module.factoryBy(
    name: String? = null,
    override: Boolean = false
): BeanDefinition<R> {
    return factory(name, override) { create<T>() as R }
}

///**
// * Create a Scope definition for given type T, applied to R
// *
// * @param scopeId
// * @param name
// * @param override - allow definition override
// */
//inline fun <reified R : Any, reified T : R> Module.scopedBy(
//    name: String? = null,
//    override: Boolean = false
//): BeanDefinition<R> {
//    return scoped(name, override) { create<T>(getCurrentScope()) as R }
//}