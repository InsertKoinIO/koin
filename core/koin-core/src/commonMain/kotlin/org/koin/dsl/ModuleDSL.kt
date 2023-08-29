/*
 * Copyright 2017-Present the original author or authors.
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
package org.koin.dsl

import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module

typealias ModuleDeclaration = Module.() -> Unit

/**
 * Authors
 * @author Arnaud Giuliani
 */

/**
 * Define a Module
 * @param createdAtStart
 *
 */
@Deprecated("'override' parameter is not used anymore. See 'allowOverride' in KoinApplication")
@KoinDslMarker
fun module(createdAtStart: Boolean = false, override: Boolean = false, moduleDeclaration: ModuleDeclaration): Module {
    val module = Module(createdAtStart)
    moduleDeclaration(module)
    return module
}

@KoinDslMarker
fun module(createdAtStart: Boolean = false, moduleDeclaration: ModuleDeclaration): Module {
    val module = Module(createdAtStart)
    moduleDeclaration(module)
    return module
}
