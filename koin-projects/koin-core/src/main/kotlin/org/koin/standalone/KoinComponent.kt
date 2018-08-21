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
package org.koin.standalone

import org.koin.core.DefinitionFilter
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition


/**
 * Koin component
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
interface KoinComponent

/**
 * inject lazily given dependency for KoinComponent
 * @param name - bean canonicalName
 * @param parameters - injection parameters
 */
inline fun <reified T> KoinComponent.inject(
    name: String = "",
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, module, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean canonicalName
 * @param parameters - injection parameters
 */
inline fun <reified T> KoinComponent.get(
    name: String = "",
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T =
    (StandAloneContext.koinContext as KoinContext).get(name, module, parameters)

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean canonicalName
 * @param parameters - injection parameters
 */
fun <T> KoinComponent.getForClass(
    name: String = "",
    className: String,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition(),
    filter: DefinitionFilter? = null
): T =
    (StandAloneContext.koinContext as KoinContext).getForClass(name, className, module, parameters, filter)

/**
 * inject lazily given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.property(key: String): Lazy<T> =
    kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * inject lazily given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.property(key: String, defaultValue: T): Lazy<T> =
    kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.getProperty(key: String): T =
    (StandAloneContext.koinContext as KoinContext).getProperty(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.getProperty(key: String, defaultValue: T): T =
    (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)

/**
 * set a property
 * @param key
 * @param value
 */
fun KoinComponent.setProperty(key: String, value: Any) = getKoinContext().setProperty(key, value)

/**
 * Release instances at given module scope
 * @param path
 */
fun KoinComponent.release(path: String) = getKoinContext().release(path)

/**
 * Release instances at given module scope
 * @param path
 */
@Deprecated("function renamed - use release() function instead", ReplaceWith("release(path)"))
fun KoinComponent.releaseContext(path: String) = getKoinContext().release(path)

/**
 * Help to Access context
 */
private fun getKoinContext() = (StandAloneContext.koinContext as KoinContext)


