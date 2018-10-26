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
package org.koin.dsl.module

import org.koin.core.KoinContext
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.path.Path

/**
 * Koin Module creation functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Create a Module
 * @Deprecated @see module
 */
@Deprecated(
    "Use the module() function instead.",
    ReplaceWith("module(definition = moduleDefinition)")
)
fun applicationContext(moduleDefinition: ModuleDefinition.() -> Unit): Module =
    module(definition = moduleDefinition)

/**
 * Create a Module
 * Gather definitions
 * @param path : Path of the module
 * @param createOnStart : module definitions will be tagged as `createOnStart`
 * @param override : allow all definitions from module to override definitions
 */
fun module(
    path: String = Path.ROOT,
    createOnStart: Boolean = false,
    override: Boolean = false,
    definition: ModuleDefinition.() -> Unit
): Module =
    { koinContext -> ModuleDefinition(path, createOnStart, override, koinContext).apply(definition) }

/**
 * Module - function that gives a module
 */
typealias Module = (KoinContext) -> ModuleDefinition