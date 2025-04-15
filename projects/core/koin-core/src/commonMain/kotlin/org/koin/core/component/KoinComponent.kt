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
package org.koin.core.component

import org.koin.core.Koin
import org.koin.core.module.mapMultibindingQualifier
import org.koin.core.module.setMultibindingQualifier
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

/**
 * KoinComponent interface marker to bring Koin extensions features
 *
 * @author Arnaud Giuliani
 */
interface KoinComponent {

    /**
     * Get the associated Koin instance
     */
    fun getKoin(): Koin = KoinPlatformTools.defaultContext().get()
}

/**
 * Get instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified T : Any> KoinComponent.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return if (this is KoinScopeComponent) {
        scope.get(qualifier, parameters)
    } else {
        getKoin().get(qualifier, parameters)
    }
}

/**
 * Lazy inject instance from Koin
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 */
inline fun <reified T : Any> KoinComponent.inject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> =
    lazy(mode) { get<T>(qualifier, parameters) }

/**
 * Get Koin Map Multibinding instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified K, reified V> KoinComponent.getMapMultibinding(
    qualifier: Qualifier = mapMultibindingQualifier<K, V>(),
    noinline parameters: ParametersDefinition? = null,
): Map<K, V> =
    get(qualifier, parameters)

/**
 * Lazy inject Koin Map Multibinding instance from Koin
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 */
inline fun <reified K, reified V> KoinComponent.injectMapMultibinding(
    qualifier: Qualifier = mapMultibindingQualifier<K, V>(),
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<Map<K, V>> =
    lazy(mode) { get<Map<K, V>>(qualifier, parameters) }

/**
 * Get Koin Set Multibinding instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified E> KoinComponent.getSetMultibinding(
    qualifier: Qualifier = setMultibindingQualifier<E>(),
    noinline parameters: ParametersDefinition? = null,
): Set<E> =
    get(qualifier, parameters)

/**
 * Lazy inject Koin Set Multibinding instance from Koin
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 */
inline fun <reified E> KoinComponent.injectSetMultibinding(
    qualifier: Qualifier = setMultibindingQualifier<E>(),
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
    noinline parameters: ParametersDefinition? = null,
): Lazy<Set<E>> =
    lazy(mode) { get<Set<E>>(qualifier, parameters) }
