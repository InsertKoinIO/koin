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
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.PropertyRegistry
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.core.time.measureDuration
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Koin
 *
 * Gather main features to use on Koin context
 *
 * @author Arnaud Giuliani
 */
class Koin {
    val beanRegistry = BeanRegistry()
    val scopeRegistry = ScopeRegistry()
    val propertyRegistry = PropertyRegistry()

    /**
     * Lazy inject a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T> inject(
        qualifier: Qualifier? = null,
        scope: Scope = Scope.GLOBAL,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
        lazy { get<T>(qualifier, scope, parameters) }

    /**
     * Get a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T> get(
        qualifier: Qualifier? = null,
        scope: Scope = Scope.GLOBAL,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return get(T::class, qualifier, scope, parameters)
    }

    /**
     * Get a Koin instance
     * @param clazz
     * @param qualifier
     * @param scope
     * @param parameters
     */
    fun <T> get(
        clazz: KClass<*>,
        qualifier: Qualifier?,
        scope: Scope = Scope.GLOBAL,
        parameters: ParametersDefinition?
    ): T = synchronized(this) {
        return if (logger.isAt(Level.DEBUG)) {
            logger.debug("+- get '${clazz.getFullName()}'")
            val (instance: T, duration: Double) = measureDuration {
                resolve<T>(qualifier, clazz, scope, parameters)
            }
            logger.debug("+- got '${clazz.getFullName()}' in $duration ms")
            return instance
        } else {
            resolve(qualifier, clazz, scope, parameters)
        }
    }

    private fun <T> resolve(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        scope: Scope,
        parameters: ParametersDefinition?
    ): T {
        val (definition, foundScope) = prepareResolution(qualifier, clazz, scope)
        return definition.resolveInstance(InstanceContext(this, foundScope, parameters))
    }

    private fun prepareResolution(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        scope: Scope
    ): Pair<BeanDefinition<*>, Scope> {
        val definition = beanRegistry.findDefinition(qualifier, clazz)
            ?: throw NoBeanDefFoundException("No definition found for '${clazz.getFullName()}' has been found. Check your module definitions.")
        return Pair(definition, scope)
    }

    internal fun createEagerInstances() {
        val definitions = beanRegistry.findAllCreatedAtStartDefinition()
        if (definitions.isNotEmpty()) {
            definitions.forEach {
                it.resolveInstance(InstanceContext(koin = this, scope = Scope.GLOBAL))
            }
        }
    }

    /**
     * Create a Scope instance
     * @param scopeId
     * @param scopeDefinitionName
     */
    @JvmOverloads
    fun createScope(scopeId: ScopeID, qualifier: Qualifier? = null): Scope {
        if (logger.isAt(Level.DEBUG)) {
            logger.debug("!- create scope - id:$scopeId q:$qualifier")
        }
        val createdScopeInstance = scopeRegistry.createScopeInstance(scopeId, qualifier)
        createdScopeInstance.register(this)
        return createdScopeInstance
    }

    /**
     * Get or Create a Scope instance
     * @param scopeId
     * @param qualifier
     */
    @JvmOverloads
    fun getOrCreateScope(scopeId: ScopeID, qualifier: Qualifier? = null): Scope {
        return scopeRegistry.getScopeInstanceOrNull(scopeId) ?: createScope(scopeId, qualifier)
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScope(scopeId: ScopeID): Scope {
        val scope = scopeRegistry.getScopeInstance(scopeId)
        if (!scope.isRegistered()) {
            error("ScopeInstance $scopeId is not registered")
        }
        return scope
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
        beanRegistry.close()
        propertyRegistry.close()
    }
}