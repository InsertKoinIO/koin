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
package org.koin.ktor.ext

import io.ktor.routing.Routing
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.StandAloneContext


/**
 * Ktor Koin extensions for Routing class
 *
 * @author Arnaud Giuliani
 * @author Laurent Baresse
 */

/**
 * inject lazily given dependency
 * @param name - bean name / optional
 * @param module - module path
 * @param parameters
 */
inline fun <reified T: Any> Routing.inject(
    name: String = "",
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) =
    lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, module, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 * @param module - module path
 * @param parameters
 */
inline fun <reified T: Any> Routing.get(
    name: String = "",
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) =
    (StandAloneContext.koinContext as KoinContext).get<T>(name, module, parameters)

/**
 * lazy inject given property
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Routing.property(key: String) =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Routing.property(key: String, defaultValue: T) =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Routing.getProperty(key: String) =
    (StandAloneContext.koinContext as KoinContext).getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Routing.getProperty(key: String, defaultValue: T) =
    (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)


/**
 * Help work on ModuleDefinition
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Set property value
 *
 * @param key - key property
 * @param value - property value
 *
 */
fun Routing.setProperty(key: String, value: Any) = context().setProperty(key, value)