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
package org.koin.spark

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition


/**
 * Declare a Spark controller
 *
 * @param name
 * @param override - allow definition override
 *
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : SparkController> ModuleDefinition.controller(
    name: String = "",
    override: Boolean = false,
    noinline definition: Definition<T>
) {
    val def = single(name, true, override, definition)
    def.bind(SparkController::class)
}

/**
 * Tag interface for Spark controllers
 */
interface SparkController