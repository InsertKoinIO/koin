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

import org.koin.core.definition.BeanDefinition
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.registry.PropertyRegistry
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import java.util.*
import kotlin.reflect.KClass

/**
 * Koin
 *
 * Gather main features to use on Koin context
 *
 * @author Arnaud Giuliani
 */
class Koin {
    val _scopeRegistry = ScopeRegistry(this)
    val _propertyRegistry = PropertyRegistry(this)
    var _logger: Logger = EmptyLogger()
    val _modules = hashSetOf<Module>()

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
    ): Lazy<T> = _scopeRegistry.rootScope.inject(qualifier, parameters)

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
    ): Lazy<T?> = _scopeRegistry.rootScope.injectOrNull(qualifier, parameters)

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
    ): T = _scopeRegistry.rootScope.get(qualifier, parameters)

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
    ): T? = _scopeRegistry.rootScope.getOrNull(qualifier, parameters)

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
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): T = _scopeRegistry.rootScope.get(clazz, qualifier, parameters)

    /**
     * Get a Koin instance if available
     * @param clazz
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T or null
     */
    fun <T> getOrNull(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): T? = _scopeRegistry.rootScope.getOrNull(clazz, qualifier, parameters)


    /**
     * Declare a component definition from the given instance
     * This result of declaring a single definition of type T, returning the given instance
     *
     * @param instance The instance you're declaring.
     * @param qualifier Qualifier for this declaration
     * @param secondaryTypes List of secondary bound types
     * @param override Allows to override a previous declaration of the same type (default to false).
     */
    inline fun <reified T : Any> declare(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        override: Boolean = false
    ) {
        val firstType = listOf(T::class)
        _scopeRegistry.rootScope.declare(instance, qualifier, firstType + secondaryTypes, override)
    }

    /**
     * Get a all instance for given inferred class (in primary or secondary type)
     *
     * @return list of instances of type T
     */
    inline fun <reified T : Any> getAll(): List<T> = _scopeRegistry.rootScope.getAll()

    /**
     * Get instance of primary type P and secondary type S
     * (not for scoped instances)
     *
     * @return instance of type S
     */
    inline fun <reified S, reified P> bind(noinline parameters: ParametersDefinition? = null): S =
        _scopeRegistry.rootScope.bind<S, P>(parameters)

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
    ): S = _scopeRegistry.rootScope.bind(primaryType, secondaryType, parameters)

    internal fun createEagerInstances() {
        _scopeRegistry.rootScope.createEagerInstances()
    }

    /**
     * Create a Scope instance
     * @param scopeId
     * @param scopeDefinitionName
     */
    fun createScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null): Scope {
        if (_logger.isAt(Level.DEBUG)) {
            _logger.debug("!- create scope - id:'$scopeId' q:$qualifier")
        }
        return _scopeRegistry.createScope(scopeId, qualifier, source)
    }

    /**
     * Create a Scope instance
     * @param scopeId
     */
    inline fun <reified T> createScope(scopeId: ScopeID, source: Any? = null): Scope {
        val qualifier = TypeQualifier(T::class)
        if (_logger.isAt(Level.DEBUG)) {
            _logger.debug("!- create scope - id:'$scopeId' q:$qualifier")
        }
        return _scopeRegistry.createScope(scopeId, qualifier, source)
    }

    /**
     * Create a Scope instance
     * @param scopeDefinitionName
     */
    inline fun <reified T> createScope(scopeId: ScopeID = UUID.randomUUID().toString()): Scope {
        val qualifier = TypeQualifier(T::class)
        if (_logger.isAt(Level.DEBUG)) {
            _logger.debug("!- create scope - id:'$scopeId' q:$qualifier")
        }
        return _scopeRegistry.createScope(scopeId, qualifier, null)
    }

    /**
     * Create a Scope instance
     * @param scopeDefinitionName
     */
    fun <T : KoinScopeComponent> createScope(t: T): Scope {
        val scopeId = t.getScopeId()
        val qualifier = t.getScopeName()
        if (_logger.isAt(Level.DEBUG)) {
            _logger.debug("!- create scope - id:'$scopeId' q:$qualifier")
        }
        return _scopeRegistry.createScope(scopeId, qualifier, null)
    }

    /**
     * Get or Create a Scope instance
     * @param scopeId
     * @param qualifier
     * @param source
     */
    fun getOrCreateScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null): Scope {
        return _scopeRegistry.getScopeOrNull(scopeId) ?: createScope(scopeId, qualifier, source)
    }

    /**
     * Get or Create a Scope instance
     * @param scopeId
     * @param qualifier
     */
    inline fun <reified T> getOrCreateScope(scopeId: ScopeID): Scope {
        val qualifier = TypeQualifier(T::class)
        return _scopeRegistry.getScopeOrNull(scopeId) ?: createScope(scopeId, qualifier)
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScope(scopeId: ScopeID): Scope {
        return _scopeRegistry.getScopeOrNull(scopeId)
            ?: throw ScopeNotCreatedException("No scope found for id '$scopeId'")
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScopeOrNull(scopeId: ScopeID): Scope? {
        return _scopeRegistry.getScopeOrNull(scopeId)
    }

    /**
     * Delete a scope instance
     */
    fun deleteScope(scopeId: ScopeID) {
        _scopeRegistry.deleteScope(scopeId)
    }

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    fun getProperty(key: String, defaultValue: String): String {
        return _propertyRegistry.getProperty(key) ?: defaultValue
    }

    /**
     * Retrieve a property
     * @param key
     */
    fun getProperty(key: String): String? {
        return _propertyRegistry.getProperty(key)
    }

    /**
     * Save a property
     * @param key
     * @param value
     */
    fun setProperty(key: String, value: String) {
        _propertyRegistry.saveProperty(key, value)
    }

    /**
     * Delete a property
     * @param key
     */
    fun deleteProperty(key: String) {
        _propertyRegistry.deleteProperty(key)
    }

    /**
     * Close all resources from context
     */
    fun close() = synchronized(this) {
        _modules.forEach { it.isLoaded = false }
        _modules.clear()
        _scopeRegistry.close()
        _propertyRegistry.close()
    }

    fun loadModules(modules: List<Module>) = synchronized(this) {
        _modules.addAll(modules)
        _scopeRegistry.loadModules(modules)
    }


    fun unloadModules(modules: List<Module>) = synchronized(this) {
        _scopeRegistry.unloadModules(modules)
        _modules.removeAll(modules)
    }
}