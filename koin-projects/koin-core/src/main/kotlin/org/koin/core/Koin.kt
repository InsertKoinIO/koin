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
import org.koin.core.definition.DefaultContext
import org.koin.core.error.BadScopeInstanceException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.PropertyRegistry
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.ScopeInstance
import org.koin.core.scope.getScopeName
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
    val defaultContext = DefaultContext(this)

    /**
     * Lazy inject a Koin instance
     * @param name
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T> inject(
            name: String? = null,
            scope: ScopeInstance = ScopeInstance.GLOBAL,
            noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
            lazy { get<T>(name, scope, parameters) }

    /**
     * Get a Koin instance
     * @param name
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T> get(
            name: String? = null,
            scope: ScopeInstance = ScopeInstance.GLOBAL,
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
            scope: ScopeInstance = ScopeInstance.GLOBAL,
            parameters: ParametersDefinition?
    ): T = synchronized(this) {
        return if (logger.level == Level.DEBUG) {
            logger.debug("+- get '${clazz.getFullName()}'")
            val (instance: T, duration: Double) = measureDuration {
                resolve<T>(name, clazz, scope, parameters)
            }
            logger.debug("+- got '${clazz.getFullName()}' in $duration ms")
            return instance
        } else {
            resolve(name, clazz, scope, parameters)
        }
    }

    private fun <T> resolve(
            name: String?,
            clazz: KClass<*>,
            scope: ScopeInstance,
            parameters: ParametersDefinition?
    ): T {
        val (definition, targetScopeInstance) = prepareResolution(name, clazz, scope)
        val instanceContext = InstanceContext(this, targetScopeInstance, parameters)
        return definition.resolveInstance(instanceContext)
    }

    private fun prepareResolution(
            name: String?,
            clazz: KClass<*>,
            scope: ScopeInstance
    ): Pair<BeanDefinition<*>, ScopeInstance> {
        val definition = beanRegistry.findDefinition(name, clazz)
                ?: throw NoBeanDefFoundException("No definition found for '${clazz.getFullName()}' has been found. Check your module definitions.")

        if (definition.isScoped() && scope != ScopeInstance.GLOBAL) {
            checkScopeResolution(definition, scope)
        }

        return Pair(definition, scope)
    }

    private fun checkScopeResolution(definition: BeanDefinition<*>, scope: ScopeInstance) {
        val scopeInstanceName = scope.definition?.scopeName
        val beanScopeName = definition.getScopeName()
        if (beanScopeName != scopeInstanceName) {
            when {
                scopeInstanceName == null -> throw BadScopeInstanceException("Can't use definition $definition defined for scope '$beanScopeName', with an open scope instance $scope. Use a scope instance with scope '$beanScopeName'")
                beanScopeName != null -> throw BadScopeInstanceException("Can't use definition $definition defined for scope '$beanScopeName' with scope instance $scope. Use a scope instance with scope '$beanScopeName'.")
            }
        }
    }

    internal fun createEagerInstances() {
        val definitions = beanRegistry.findAllCreatedAtStartDefinition()
        if (definitions.isNotEmpty()) {
            definitions.forEach {
                it.resolveInstance(InstanceContext(koin = this, scope = ScopeInstance.GLOBAL))
            }
        }
    }

    /**
     * Create a Scope instance
     * @param scopeId
     * @param scopeDefinitionName
     */
    @JvmOverloads
    fun createScope(scopeId: String, scopeDefinitionName: String? = null): ScopeInstance {
        val createdScopeInstance = scopeRegistry.createScopeInstance(scopeId, scopeDefinitionName)
        createdScopeInstance.register(this)
        return createdScopeInstance
    }

    /**
     * Create a Scope instance
     * @param scopeId
     */
    inline fun <reified T> createScopeWithType(scopeId: String): ScopeInstance {
        val scopeName = T::class.getFullName()
        val createdScopeInstance = scopeRegistry.createScopeInstance(scopeId, scopeName)
        createdScopeInstance.register(this)
        return createdScopeInstance
    }

    /**
     * Get or Create a Scope instance
     * @param scopeId
     * @param scopeName
     */
    @JvmOverloads
    fun getOrCreateScope(scopeId: String, scopeName: String? = null): ScopeInstance {
        return scopeRegistry.getScopeInstanceOrNull(scopeId) ?: createScope(scopeId, scopeName)
    }

    /**
     * Get or Create a Scope instance from type name
     * @param scopeId
     */
    inline fun <reified T> getOrCreateScopeWithType(scopeId: String): ScopeInstance {
        val scopeName = T::class.getFullName()
        return scopeRegistry.getScopeInstanceOrNull(scopeId) ?: createScope(scopeId, scopeName)
    }

    /**
     * get a scope instance
     * @param scopeId
     */
    fun getScope(scopeId: String): ScopeInstance {
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
    fun getScopeOrNull(scopeId: String): ScopeInstance? {
        return scopeRegistry.getScopeInstanceOrNull(scopeId)
    }

    /**
     * Delete a scope instance
     */
    fun deleteScope(scopeId: String) {
        scopeRegistry.deleteScopeInstance(scopeId)
    }

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    @JvmOverloads
    fun <T> getProperty(key: String, defaultValue: T? = null): T? {
        return propertyRegistry.getProperty<T>(key) ?: defaultValue
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