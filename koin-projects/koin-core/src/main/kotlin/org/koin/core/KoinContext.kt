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

import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.InstanceRegistry
import org.koin.core.instance.InstanceRequest
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.property.PropertyRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeRegistry
import org.koin.error.MissingPropertyException
import org.koin.error.NoScopeFoundException
import org.koin.standalone.StandAloneKoinContext
import kotlin.reflect.KClass


/**
 * Koin Application ModuleDefinition
 * ModuleDefinition from where you can get beans defined in modules
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent Baresse
 */
class KoinContext private constructor(
    val instanceRegistry: InstanceRegistry,
    val scopeRegistry: ScopeRegistry,
    val propertyResolver: PropertyRegistry
) : StandAloneKoinContext {

    /**
     * Retrieve an instance from its name/class
     * @param name
     * @param scope
     * @param parameters
     */
    inline fun <reified T : Any> get(
        name: String = "",
        scope: Scope? = null,
        noinline parameters: ParameterDefinition = emptyParameterDefinition()
    ): T = instanceRegistry.resolve(
        InstanceRequest(
            name = name,
            clazz = T::class,
            scope = scope,
            parameters = parameters
        )
    )

    /**
     * Retrieve an instance from its name/class
     *
     * @param name
     * @param clazz
     * @param scope
     * @param parameters
     */
    fun <T : Any> get(
        name: String = "",
        clazz: KClass<*>,
        scope: Scope? = null,
        parameters: ParameterDefinition = emptyParameterDefinition()
    ): T = instanceRegistry.resolve(
        InstanceRequest(
            name = name,
            clazz = clazz,
            scope = scope,
            parameters = parameters
        )
    )

    /**
     * Create a scope
     */
    fun createScope(id: String): Scope = scopeRegistry.createScope(id)

    /**
     * Create a scope
     */
    fun getOrCreateScope(id: String): Scope = scopeRegistry.getOrCreateScope(id)


    /**
     * Create a detached scope
     */
    fun detachScope(id: String): Scope = scopeRegistry.createAndDetachScope(id)

    /**
     * retrieve a scope
     */
    fun getScope(id: String): Scope =
        scopeRegistry.getScope(id) ?: throw NoScopeFoundException("Scope '$id' not found")

    /**
     * Drop all instances for path context
     * @param path
     */
    @Deprecated("Please use Scope API.")
    fun release(path: String) {
        val p = instanceRegistry.pathRegistry.getPath(path)
        instanceRegistry.instanceFactory.releasePath(p)
    }

    /**
     * Retrieve a property by its key
     * can throw MissingPropertyException if the property is not found
     * @param key
     * @throws MissingPropertyException if key is not found
     */
    fun <T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Retrieve a property by its key or return provided default value
     * @param key - property key
     * @param defaultValue - default value if property is not found
     */
    fun <T> getProperty(key: String, defaultValue: T): T =
        propertyResolver.getProperty(key, defaultValue)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.add(key, value)

    /**
     * Close all resources
     */
    fun close() {
        instanceRegistry.close()
        scopeRegistry.close()
        propertyResolver.clear()
    }

    companion object {

        /**
         * Create context instance
         */
        fun create(instanceFactory: InstanceFactory = InstanceFactory()): KoinContext {
            val propertyResolver = PropertyRegistry()
            val scopeRegistry = ScopeRegistry()
            val instanceResolver = InstanceRegistry(
                BeanRegistry(),
                instanceFactory,
                PathRegistry(),
                scopeRegistry
            )
            return KoinContext(instanceResolver, scopeRegistry, propertyResolver)
        }
    }
}