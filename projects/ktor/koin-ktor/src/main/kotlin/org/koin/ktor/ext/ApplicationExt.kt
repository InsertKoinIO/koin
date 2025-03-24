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
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.ktor.plugin.KOIN_ATTRIBUTE_KEY
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.setKoinApplication

/**
 * Ktor Koin extensions
 *
 * @author Arnaud Giuliani
 * @author Laurent Baresse
 */



/**
 * Help work on ModuleDefinition
 */
fun Application.getKoin(): Koin =
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)?.koin ?: run {
        val defaultInstance = GlobalContext.getKoinApplicationOrNull() ?: error("No Koin instance started. Use install(Koin) or startKoin()")
        setKoinApplication(defaultInstance)
        attributes[KOIN_ATTRIBUTE_KEY].koin
    }

/**
 * inject lazily given dependency
 * @param qualifier - bean name / optional
 * @param scope
 * @param parameters
 */
inline fun <reified T : Any> Application.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    lazy { get<T>(qualifier, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param qualifier - bean name / optional
 * @param scope
 * @param parameters
 */
inline fun <reified T : Any> Application.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    getKoin().get<T>(qualifier, parameters)

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 */
fun <T : Any> Application.getProperty(key: String) =
    getKoin().getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
fun Application.getProperty(key: String, defaultValue: String) =
    getKoin().getProperty(key) ?: defaultValue
