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

/**
 * Build instance with Koin injected dependencies
 */
inline fun <reified T : Any> ModuleDefinition.build(): T {
    val clazz = T::class.java
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { get(clazz = it) }.toTypedArray()
    return ctor.newInstance(*args) as T
}