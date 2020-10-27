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
package org.koin.core.component

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@RequiresOptIn(message = "Used to extend current API with Koin API",level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class KoinApiExtension

/**
 * KoinComponent interface marker to bring Koin extensions features
 *
 * @author Arnaud Giuliani
 */
@KoinApiExtension
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
@OptIn(KoinApiExtension::class)
inline fun <reified T : Any> KoinComponent.get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T =
        getKoin().get(qualifier, parameters)

/**
 * Lazy inject instance from Koin
 * @param qualifier
 * @param parameters
 */
@OptIn(KoinApiExtension::class)
inline fun <reified T : Any> KoinComponent.inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE) { getKoin().get<T>(qualifier, parameters) }

/**
 * Get instance instance from Koin by Primary Type P, as secondary type S
 * @param parameters
 */
@OptIn(KoinApiExtension::class)
inline fun <reified S : Any, reified P : Any> KoinComponent.bind(
        noinline parameters: ParametersDefinition? = null
): S =
        getKoin().bind<S, P>(parameters)
