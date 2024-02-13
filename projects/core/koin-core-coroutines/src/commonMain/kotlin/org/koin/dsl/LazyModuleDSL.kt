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

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module

/**
 * Authors
 * @author Arnaud Giuliani
 */

/**
 * Define a Koin module as Lazy way, to not resolved resources before loading it
 *
 * @See lazyModules() function, to load Lazy module in background
 */
@KoinExperimentalAPI
@KoinDslMarker
fun lazyModule(moduleDefinition: ModuleDeclaration): Lazy<Module> = lazy(LazyThreadSafetyMode.NONE) { module(moduleDeclaration = moduleDefinition) }
