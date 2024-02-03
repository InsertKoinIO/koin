/*
 * Copyright 2024-Present the original author or authors.
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

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Provides access to Koin instances
 *
 * @author Andrei Nevedomskii
 */
interface Instances {

    /**
     * Get a all instance for given class (in primary or secondary type)
     * @param clazz T
     *
     * @return list of instances of type T
     */
    fun <T> getAll(clazz: KClass<*>): List<T>

    /**
     * Get a Koin instance if available
     * @param clazz type of the instance to get
     * @param qualifier
     * @param parameters
     *
     * @return instance of type T or null
     */
    fun <T> getOrNull(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null,
    ): T?

    /**
     * Get a Koin instance
     * @param clazz type of the instance to get
     * @param qualifier
     * @param parameters
     */
    fun <T> get(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null,
    ): T

    /**
     * Lazy inject a Koin instance if available
     * @param clazz type of the instance to get
     * @param qualifier
     * @param mode - LazyThreadSafetyMode
     * @param parameters
     *
     * @return Lazy instance of type T or null
     */
    fun <T> injectOrNull(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        parameters: ParametersDefinition? = null,
    ): Lazy<T?>

    /**
     * Lazy inject a Koin instance
     * @param clazz type of the instance to inject
     * @param qualifier
     * @param mode - LazyThreadSafetyMode
     * @param parameters
     *
     * @return Lazy instance of type T
     */
    fun <T> inject(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        parameters: ParametersDefinition? = null,
    ): Lazy<T>

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    fun <T : Any> getProperty(key: String, defaultValue: T): T

    /**
     * Retrieve a property
     * @param key
     */
    fun <T : Any> getProperty(key: String): T

    /**
     * Retrieve a property
     * @param key
     */
    fun <T : Any> getPropertyOrNull(key: String): T?
}

/**
 * Get a all instance for given inferred class (in primary or secondary type)
 *
 * @return list of instances of type T
 */
inline fun <reified T> Instances.getAll() = getAll<T>(T::class)

/**
 * Get a Koin instance if available
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T or null
 */
inline fun <reified T> Instances.getOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = getOrNull<T>(T::class, qualifier, parameters)

/**
 * Get a Koin instance
 * @param qualifier
 * @param parameters
 */
inline fun <reified T> Instances.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = get<T>(T::class, qualifier, parameters)

/**
 * Lazy inject a Koin instance if available
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 *
 * @return Lazy instance of type T or null
 */
inline fun <reified T> Instances.injectOrNull(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline parameters: ParametersDefinition? = null,
) = injectOrNull<T>(T::class, qualifier, mode, parameters)

/**
 * Lazy inject a Koin instance
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 *
 * @return Lazy instance of type T
 */
inline fun <reified T> Instances.inject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline parameters: ParametersDefinition? = null,
) = inject<T>(T::class, qualifier, mode, parameters)