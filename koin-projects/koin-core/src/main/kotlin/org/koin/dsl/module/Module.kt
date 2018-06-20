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
import org.koin.standalone.StandAloneContext


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
    "use module function instead", ReplaceWith(
        "module { ModuleDefinition(Path.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }",
        "org.koin.dsl.context.ModuleDefinition",
        "org.koin.core.path.Path",
        "org.koin.standalone.StandAloneContext",
        "org.koin.core.KoinContext"
    )
)
fun applicationContext(init: ModuleDefinition.() -> Unit): Module = module(Path.ROOT, false, false, init)

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
    init: ModuleDefinition.() -> Unit
): Module =
    { ModuleDefinition(path, createOnStart, override, StandAloneContext.koinContext as KoinContext).apply(init) }

/**
 * Module - function that gives a module
 */
typealias Module = () -> ModuleDefinition