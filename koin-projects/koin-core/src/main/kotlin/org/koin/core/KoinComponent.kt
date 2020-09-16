/*
 * Copyright 2017-2020 the original author or authors.
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
package org.koin.core

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * KoinComponent interface marker to bring Koin extensions features
 *
 * @author Arnaud Giuliani
 */
interface KoinComponent {

    /**
     * Get the associated Koin instance
     */
    fun getKoin(): Koin = GlobalContext.get()
}

/**
 * Get instance instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified T> KoinComponent.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T =
    getKoin().get(qualifier, parameters)

/**
 * Get a Koin instance if available
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T or null
 */
inline fun <reified T> KoinComponent.getOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T? =
    getKoin().getOrNull(qualifier, parameters)

/**
 * Lazy inject instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified T> KoinComponent.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getKoin().get<T>(qualifier, parameters) }

/**
 * Lazy inject a Koin instance if available
 * @param qualifier
 * @param parameters
 *
 * @return Lazy instance of type T or null
 */
inline fun <reified T> KoinComponent.injectOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T?> =
    getKoin().injectOrNull(qualifier, parameters)

/**
 * Get instance instance from Koin by Primary Type P, as secondary type S
 * @param parameters
 */
inline fun <reified S, reified P> KoinComponent.bind(
    noinline parameters: ParametersDefinition? = null
): S =
    getKoin().bind<S, P>(parameters)
