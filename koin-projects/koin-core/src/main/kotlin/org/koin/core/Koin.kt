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
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.instance.InstanceResolver
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.PropertyRegistry
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.Scope
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
    private val instanceResolver = InstanceResolver()
    val scopeRegistry = ScopeRegistry()
    val propertyRegistry = PropertyRegistry()

    /**
     * Lazy inject a Koin instance
     * @param name
     * @param scope
     * @param parameters
     */
    inline fun <reified T> inject(
        name: String? = null,
        scope: Scope? = null,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
        lazy { get<T>(name, scope, parameters) }

    /**
     * Get a Koin instance
     * @param name
     * @param scope
     * @param parameters
     */
    inline fun <reified T> get(
        name: String? = null,
        scope: Scope? = null,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return get(T::class, name, scope, parameters)
    }

    /**
     * Get a Koin instance
     * @param clazz
     * @param name
     * @param scope
     * @param parameters
     */
    fun <T> get(
        clazz: KClass<*>,
        name: String?,
        scope: Scope?,
        parameters: ParametersDefinition?
    ): T = synchronized(this) {
        logger.debug("+- get '${clazz.getFullName()}'")

        val (instance: T, duration: Double) = measureDuration {
            val (definition, targetScope) = prepareResolution(name, clazz, scope)
            resolveInstance<T>(definition, targetScope, parameters)
        }

        logger.debug("+- got '${clazz.getFullName()}' in $duration ms")
        return instance
    }

    private fun <T> resolveInstance(
        definition: BeanDefinition<*>,
        targetScope: Scope?,
        parameters: ParametersDefinition?
    ) = instanceResolver.resolveInstance(definition, targetScope, parameters) as T

    private fun prepareResolution(
        name: String?,
        clazz: KClass<*>,
        scope: Scope?
    ): Pair<BeanDefinition<*>, Scope?> {
        val definition = beanRegistry.findDefinition(name, clazz)
                ?: throw NoBeanDefFoundException("No definition found for '${clazz.getFullName()}' has been found. Check your module definitions.")

        val targetScope = scopeRegistry.prepareScope(definition, scope)
        return Pair(definition, targetScope)
    }

    internal fun createEagerInstances() {
        val definitions = beanRegistry.findAllCreatedAtStartDefinition()
        if (definitions.isNotEmpty()) {
            definitions.forEach {
                instanceResolver.resolveInstance(it, null, null)
            }
        }
    }

    /**
     * Create a Scope
     * @param scopeId
     */
    fun createScope(scopeId: String): Scope {
        val createdScope = scopeRegistry.createScope(scopeId)
        createdScope.register(this)
        return createdScope
    }

    /**
     * Create or retrieve a scope
     * @param scopeId
     */
    fun getScope(scopeId: String): Scope {
        val scope = scopeRegistry.getScopeById(scopeId)
                ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created")
        scope.register(this)
        return scope
    }

    /**
     * Create or retrieve a scope
     * @param scopeId
     */
    fun getOrCreateScope(scopeId: String): Scope {
        val scope = scopeRegistry.getOrCreateScope(scopeId)
        if (!scope.isRegistered()) {
            scope.register(this)
        }
        return scope
    }

    /**
     * Detach a scope
     * @param scopeId
     */
    fun detachScope(scopeId: String): Scope {
        val createdScope = scopeRegistry.detachScope(scopeId)
        createdScope.register(this)
        return createdScope
    }

    /**
     * Retrieve detached scope
     * @param internalId
     */
    fun getDetachedScope(internalId: String): Scope? {
        return scopeRegistry.getScopeByInternalId(internalId)
    }

    internal fun closeScope(internalId: String) {
        val scope: Scope =
            scopeRegistry.getScopeByInternalId(internalId) ?: error("Scope not found '$internalId'")
        beanRegistry.releaseInstanceForScope(scope)
        scopeRegistry.deleteScope(scope)
    }

    /**
     * Retrieve a property
     * @param key
     */
    fun <T> getProperty(key: String): T? {
        return propertyRegistry.getProperty<T>(key)
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
        beanRegistry.close()
        instanceResolver.close()
        scopeRegistry.close()
        propertyRegistry.close()
    }
}