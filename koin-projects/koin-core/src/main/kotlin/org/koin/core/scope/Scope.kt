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
package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.component.KoinApiExtension
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.indexKey
import org.koin.core.error.ClosedScopeException
import org.koin.core.error.MissingPropertyException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.registry.InstanceRegistry
import org.koin.core.time.measureDurationForResult
import org.koin.ext.getFullName
import kotlin.reflect.KClass

data class Scope(
        val id: ScopeID,
        @KoinApiExtension
        val _scopeDefinition: ScopeDefinition,
        private val _koin: Koin
) {
    @PublishedApi
    internal val _linkedScope: ArrayList<Scope> = arrayListOf()

    @PublishedApi
    internal val _instanceRegistry = InstanceRegistry(_koin, this)

    @PublishedApi
    internal var _source: Any? = null

    val closed: Boolean
        get() = _closed
    private val _callbacks = arrayListOf<ScopeCallback>()
    private var _closed: Boolean = false
    private var _parameters: DefinitionParameters? = null

    val logger = _koin.logger

    internal fun create(links: List<Scope>) {
        _instanceRegistry.create(_scopeDefinition.definitions)
        _linkedScope.addAll(links)
    }

    inline fun <reified T : Any> getSource(): T = _source as? T ?: error(
            "Can't use Scope source for ${T::class.getFullName()} - source is:$_source")

    @KoinApiExtension
    fun setSource(t: Any?) {
        _source = t
    }

    /**
     * Add parent Scopes to allow instance resolution
     * i.e: linkTo(scopeC) - allow to resolve instance to current scope and scopeC
     *
     * @param scopes - Scopes to link with
     */
    fun linkTo(vararg scopes: Scope) {
        if (!_scopeDefinition.isRoot) {
            _linkedScope.addAll(scopes)
        } else {
            error("Can't add scope link to a root scope")
        }
    }

    /**
     * Remove linked scope
     */
    fun unlink(vararg scopes: Scope) {
        if (!_scopeDefinition.isRoot) {
            _linkedScope.removeAll(scopes)
        } else {
            error("Can't remove scope link to a root scope")
        }
    }

    /**
     * Lazy inject a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return Lazy instance of type T
     */
    @JvmOverloads
    inline fun <reified T : Any> inject(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
            lazy { get<T>(qualifier, parameters) }

    /**
     * Lazy inject a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return Lazy instance of type T or null
     */
    @JvmOverloads
    inline fun <reified T : Any> injectOrNull(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): Lazy<T?> =
            lazy { getOrNull<T>(qualifier, parameters) }

    /**
     * Get a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    @JvmOverloads
    inline fun <reified T : Any> get(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return get(T::class, qualifier, parameters)
    }

    /**
     * Get a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T or null
     */
    @JvmOverloads
    inline fun <reified T : Any> getOrNull(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T? {
        return getOrNull(T::class, qualifier, parameters)
    }

    /**
     * Get a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T or null
     */
    @JvmOverloads
    fun <T : Any> getOrNull(
            clazz: KClass<T>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null
    ): T? {
        return try {
            get(clazz, qualifier, parameters)
        } catch (e: ClosedScopeException) {
            _koin.logger.debug("Koin.getOrNull - scope closed - no instance found for ${clazz.getFullName()} on scope ${toString()}")
            null
        } catch (e: NoBeanDefFoundException) {
            _koin.logger.debug("Koin.getOrNull - no instance found for ${clazz.getFullName()} on scope ${toString()}")
            null
        }
    }

    /**
     * Get a Koin instance
     * @param clazz
     * @param qualifier
     * @param parameters
     *
     * @return instance of type T
     */
    fun <T : Any> get(
            clazz: KClass<T>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null
    ): T {
        return if (_koin.logger.isAt(Level.DEBUG)) {
            val qualifierString = qualifier?.let { " with qualifier '$qualifier'" } ?: ""
            _koin.logger.debug("+- '${clazz.getFullName()}'$qualifierString")
            val (instance: T, duration: Double) = measureDurationForResult {
                resolveInstance<T>(qualifier, clazz, parameters)
            }
            _koin.logger.debug("|- '${clazz.getFullName()}' in $duration ms")
            return instance
        } else {
            resolveInstance(qualifier, clazz, parameters)
        }
    }

    /**
     * Get a Koin instance
     * @param java class
     * @param qualifier
     * @param parameters
     *
     * @return instance of type T
     */
    @JvmOverloads
    fun <T : Any> get(
            clazz: Class<T>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null
    ): T {
        val kClass = clazz.kotlin
        return get(kClass, qualifier, parameters)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> resolveInstance(
            qualifier: Qualifier?,
            clazz: KClass<T>,
            parameters: ParametersDefinition?
    ): T {
        if (_closed) {
            throw ClosedScopeException("Scope '$id' is closed")
        }
        val indexKey = indexKey(clazz, qualifier)
        return _instanceRegistry.resolveInstance(indexKey, parameters)
                ?: run {
                    _koin.logger.debug("'${clazz.getFullName()}' - q:'$qualifier' not found in current scope")
                    getFromSource(clazz)
                }
                ?: run {
                    _koin.logger.debug("'${clazz.getFullName()}' - q:'$qualifier' not found in current scope's source")
                    _parameters?.getOrNull<T>(clazz)
                }
                ?: run {
                    _koin.logger.debug("'${clazz.getFullName()}' - q:'$qualifier' not found in injected parameters")
                    findInOtherScope<T>(clazz, qualifier, parameters)
                }
                ?: run {
                    _koin.logger.debug("'${clazz.getFullName()}' - q:'$qualifier' not found in linked scopes")
                    throwDefinitionNotFound(qualifier, clazz)
                }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getFromSource(clazz: KClass<*>): T? {
        return if (clazz.isInstance(_source)) _source as? T else null
    }

    private fun <T : Any> findInOtherScope(
            clazz: KClass<T>,
            qualifier: Qualifier?,
            parameters: ParametersDefinition?
    ): T? {
        var instance: T? = null
        for (scope in _linkedScope) {
            instance = scope.getOrNull<T>(
                    clazz,
                    qualifier,
                    parameters
            )
            if (instance != null) break
        }
        return instance
    }

    private fun throwDefinitionNotFound(
            qualifier: Qualifier?,
            clazz: KClass<*>
    ): Nothing {
        val qualifierString = qualifier?.let { " & qualifier:'$qualifier'" } ?: ""
        throw NoBeanDefFoundException(
                "No definition found for class:'${clazz.getFullName()}'$qualifierString. Check your definitions!")
    }

    internal fun createEagerInstances() {
        if (_scopeDefinition.isRoot) {
            _instanceRegistry.createEagerInstances()
        }
    }

    /**
     * Declare a component definition from the given instance
     * This result of declaring a scoped/single definition of type T, returning the given instance
     * (single definition of the current scope is root)
     *
     * @param instance The instance you're declaring.
     * @param qualifier Qualifier for this declaration
     * @param secondaryTypes List of secondary bound types
     * @param override Allows to override a previous declaration of the same type (default to false).
     */
    inline fun <reified T : Any> declare(
            instance: T,
            qualifier: Qualifier? = null,
            secondaryTypes: List<KClass<*>>? = null,
            override: Boolean = false
    ) = synchronized(this) {
        val definition = _scopeDefinition.saveNewDefinition(instance, qualifier, secondaryTypes, override)
        _instanceRegistry.saveDefinition(definition, override = true)
    }

    /**
     * Get current Koin instance
     */
    fun getKoin() = _koin

    /**
     * Get Scope
     * @param scopeID
     */
    fun getScope(scopeID: ScopeID) = getKoin().getScope(scopeID)

    /**
     * Register a callback for this Scope Instance
     */
    fun registerCallback(callback: ScopeCallback) {
        _callbacks += callback
    }

    /**
     * Get a all instance for given inferred class (in primary or secondary type)
     *
     * @return list of instances of type T
     */
    inline fun <reified T : Any> getAll(): List<T> = getAll(T::class)

    /**
     * Get a all instance for given class (in primary or secondary type)
     * @param clazz T
     *
     * @return list of instances of type T
     */
    fun <T : Any> getAll(clazz: KClass<*>): List<T> = _instanceRegistry.getAll(clazz)

    /**
     * Get instance of primary type P and secondary type S
     * (not for scoped instances)
     *
     * @return instance of type S
     */
    inline fun <reified S, reified P> bind(noinline parameters: ParametersDefinition? = null): S {
        val secondaryType = S::class
        val primaryType = P::class
        return bind(primaryType, secondaryType, parameters)
    }

    /**
     * Get instance of primary type P and secondary type S
     * (not for scoped instances)
     *
     * @return instance of type S
     */
    fun <S> bind(
            primaryType: KClass<*>,
            secondaryType: KClass<*>,
            parameters: ParametersDefinition?
    ): S {
        return _instanceRegistry.bind(primaryType, secondaryType, parameters)
                ?: throw NoBeanDefFoundException(
                        "No definition found to bind class:'${primaryType.getFullName()}' & secondary type:'${secondaryType.getFullName()}'. Check your definitions!")
    }

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    fun getProperty(key: String, defaultValue: String): String = _koin.getProperty(key, defaultValue)

    /**
     * Retrieve a property
     * @param key
     */
    fun getPropertyOrNull(key: String): String? = _koin.getProperty(key)

    /**
     * Retrieve a property
     * @param key
     */
    fun getProperty(key: String): String = _koin.getProperty(key)
            ?: throw MissingPropertyException("Property '$key' not found")

    /**
     * Close all instances from this scope
     */
    fun close() = synchronized(this) {
        clear()
        _koin._scopeRegistry.deleteScope(this)
    }

    internal fun clear() {
        _closed = true
        _source = null
        if (_koin.logger.isAt(Level.DEBUG)) {
            _koin.logger.info("closing scope:'$id'")
        }
        // call on close from callbacks
        _callbacks.forEach { it.onScopeClose(this) }
        _callbacks.clear()

        _instanceRegistry.close()
    }

    override fun toString(): String {
        return "['$id']"
    }

    fun dropInstance(beanDefinition: BeanDefinition<*>) {
        _instanceRegistry.dropDefinition(beanDefinition)
    }

    fun loadDefinition(beanDefinition: BeanDefinition<*>) {
        _instanceRegistry.createDefinition(beanDefinition)
    }

    fun addParameters(parameters: DefinitionParameters) {
        _parameters = parameters
    }

    fun clearParameters() {
        _parameters = null
    }
}

typealias ScopeID = String
