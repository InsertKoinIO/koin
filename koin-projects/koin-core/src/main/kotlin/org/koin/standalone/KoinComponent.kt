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

import org.koin.core.KoinContext
import org.koin.core.instance.DefinitionFilter
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.Scope
import kotlin.reflect.KClass


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
inline fun <reified T: Any> KoinComponent.inject(
    name: String = "",
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { getKoin().get<T>(name, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean canonicalName
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinComponent.get(
    name: String = "",
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T =
    getKoin().get(name, parameters)

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean canonicalName
 * @param parameters - injection parameters
 */
fun <T: Any> KoinComponent.get(
    name: String = "",
    clazz: KClass<*>,
    parameters: ParameterDefinition = emptyParameterDefinition(),
    filter: DefinitionFilter? = null
): T =
    getKoin().get(name, clazz, parameters, filter)

/**
 * inject lazily given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.property(key: String): Lazy<T> =
    kotlin.lazy { getKoin().getProperty<T>(key) }

/**
 * inject lazily given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.property(key: String, defaultValue: T): Lazy<T> =
    kotlin.lazy { getKoin().getProperty(key, defaultValue) }

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.getProperty(key: String): T =
    getKoin().getProperty(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.getProperty(key: String, defaultValue: T): T =
    getKoin().getProperty(key, defaultValue)

/**
 * set a property
 * @param key
 * @param value
 */
fun KoinComponent.setProperty(key: String, value: Any) = getKoin().setProperty(key, value)

/**
 * Release instances at given module scope
 * @param path
 *
 * Deprecated - use the Scope API instead
 */
@Deprecated("Please use the new Scope API instead.")
fun KoinComponent.release(path: String): Unit = getKoin().release(path)

/**
 * Release instances at given module scope
 * @param path
 */
@Deprecated("Please use Scope API.")
fun KoinComponent.releaseContext(path: String) : Unit = release(path)

/**
 * Access to Koin context
 */
fun KoinComponent.getKoin() : KoinContext = (StandAloneContext.koinContext as KoinContext)


