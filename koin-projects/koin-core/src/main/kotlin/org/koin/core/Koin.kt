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
package org.koin.core

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.registry.PropertyRegistry
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import kotlin.reflect.KClass

/**
 * Koin
 *
 * Gather main features to use on Koin context
 *
 * @author Arnaud Giuliani
 */
class Koin {
    val scopeRegistry = ScopeRegistry()
    val propertyRegistry = PropertyRegistry()
    val rootScope = Scope("-Root-", isRoot = true, _koin = this)

    /**
     * Lazy inject a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return Lazy instance of type T
     */
    @JvmOverloads
    inline fun <reified T> inject(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): Lazy<T> = rootScope.inject(qualifier, parameters)

    /**
     * Lazy inject a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return Lazy instance of type T or null
     */
    @JvmOverloads
    inline fun <reified T> injectOrNull(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): Lazy<T?> = rootScope.injectOrNull(qualifier, parameters)

    /**
     * Get a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T> get(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T = rootScope.get(qualifier, parameters)

    /**
     * Get a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T or null
     */
    @JvmOverloads
    inline fun <reified T> getOrNull(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T? = rootScope.getOrNull(qualifier, parameters)

    /**
     * Get a Koin instance
     * @param clazz
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T
     */
    fun <T> get(
            clazz: KClass<*>,
            qualifier: Qualifier?,
            parameters: ParametersDefinition?
    ): T = rootScope.get(clazz, qualifier, parameters)


    /**
     * Declare a component definition from the given instance
     * This result of declaring a single definition of type T, returning the given instance
     *
     * @param instance
     * @param qualifier
     */
    inline fun <reified T> declare(
            instance: T,
            qualifier: Qualifier? = null,
            secondaryTypes: List<KClass<*>>? = null
    ) {
        rootScope.declare(instance, qualifier, secondaryTypes)
    }

    /**
     * Get a all instance for given inferred class (in primary or secondary type)
     *
     * @return list of instances of type T
     */
    inline fun <reified T> getAll(): List<T> = rootScope.getAll()

    /**
     * Get instance of primary type P and secondary type S
     * (not for scoped instances)
     *
     * @return instance of type S
     */
    inline fun <reified S, reified P> bind(noinline parameters: ParametersDefinition? = null): S =
            rootScope.bind<S, P>(parameters)

    /**
     * Get instance of primary type P and secondary type S
     * (not for scoped instances)
     *
     * @return instance of type S
     */
    fun <S> bind(
            primaryType: KClass<*>,
            secondaryType: KClass<*>,
            parameters: ParametersDefinition? = null
    ): S = rootScope.bind(primaryType, secondaryType, parameters)

    internal fun createEagerInstances() = rootScope.createEagerInstances()

    /**
     * Create a Scope instance
     * @param scopeId
     * @param scopeDefinitionName
     */
    fun createScope(scopeId: ScopeID, qualifier: Qualifier): Scope {
        if (logger.isAt(Level.DEBUG)) {
            logger.debug("!- create scope - id:$scopeId q:$qualifier")
        }
        return scopeRegistry.createScopeInstance(this, scopeId, qualifier)
    }

    /**
     * Get or Create a Scope instance
     * @param scopeId
     * @param qualifier
     */
    fun getOrCreateScope(scopeId: ScopeID, qualifier: Qualifier): Scope {
        return scopeRegistry.getScopeInstanceOrNull(scopeId) ?: createScope(scopeId, qualifier)
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScope(scopeId: ScopeID): Scope {
        return scopeRegistry.getScopeInstance(scopeId)
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScopeOrNull(scopeId: ScopeID): Scope? {
        return scopeRegistry.getScopeInstanceOrNull(scopeId)
    }

    /**
     * Delete a scope instance
     */
    fun deleteScope(scopeId: ScopeID) {
        scopeRegistry.deleteScopeInstance(scopeId)
    }

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    fun <T> getProperty(key: String, defaultValue: T): T {
        return propertyRegistry.getProperty<T>(key) ?: defaultValue
    }

    /**
     * Retrieve a property
     * @param key
     */
    fun <T> getProperty(key: String): T? {
        return propertyRegistry.getProperty(key)
    }

    /**
     * Save a property
     * @param key
     * @param value
     */
    fun <T : Any> setProperty(key: String, value: T) {
        propertyRegistry.saveProperty(key, value)
    }

    /**
     * Close all resources from context
     */
    fun close() {
        scopeRegistry.close()
        rootScope.close()
        propertyRegistry.close()
    }
}