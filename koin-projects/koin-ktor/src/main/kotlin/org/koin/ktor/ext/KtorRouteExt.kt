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

import io.ktor.routing.Route
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.Scope
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
inline fun <reified T : Any> Route.inject(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) =
    lazy { get<T>(name, scope, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 * @param module - module path
 * @param parameters
 */
inline fun <reified T : Any> Route.get(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) =
    getKoin().get<T>(name, scope, parameters)

/**
 * lazy inject given property
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Route.property(key: String) =
    lazy { getKoin().getProperty<T>(key) }

/**
 * lazy inject  given property
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Route.property(key: String, defaultValue: T) =
    lazy { getKoin().getProperty(key, defaultValue) }

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Route.getProperty(key: String) =
    getKoin().getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Route.getProperty(key: String, defaultValue: T) =
    getKoin().getProperty(key, defaultValue)

/**
 * Help work on ModuleDefinition
 */
fun Route.getKoin() = (StandAloneContext.koinContext as KoinContext)

/**
 * Set property value
 *
 * @param key - key property
 * @param value - property value
 *
 */
fun Route.setProperty(key: String, value: Any) = getKoin().setProperty(key, value)