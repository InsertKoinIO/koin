/*
 * Copyright 2017-present the original author or authors.
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

import io.ktor.server.application.*
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Ktor Koin extensions for ApplicationCall class
 *
 * @author Gopal Sharma
 */

/**
 * inject lazily given dependency
 * @param qualifier - bean name / optional
 * @param parameters
 */
inline fun <reified T : Any> ApplicationCall.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    lazy { get<T>(qualifier, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param qualifier - bean name / optional
 * @param parameters
 */
inline fun <reified T : Any> ApplicationCall.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    getKoin().get<T>(qualifier, parameters)

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 */
fun <T: Any> ApplicationCall.getProperty(key: String) =
    getKoin().getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
fun ApplicationCall.getProperty(key: String, defaultValue: String) =
    getKoin().getProperty(key) ?: defaultValue

/**
 * Help work on ModuleDefinition
 */
fun ApplicationCall.getKoin(): Koin = application.getKoin()
