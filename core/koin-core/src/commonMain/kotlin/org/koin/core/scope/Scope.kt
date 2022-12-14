/*
 * Copyright 2017-2021 the original author or authors.
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
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.ClosedScopeException
import org.koin.core.error.MissingPropertyException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.module.KoinDslMarker
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.time.Timer
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatformTimeTools
import org.koin.mp.KoinPlatformTools
import org.koin.mp.Lockable
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
@KoinDslMarker
data class Scope(
    val scopeQualifier: Qualifier,
    val id: ScopeID,
    val isRoot: Boolean = false,
    @PublishedApi
    internal val _koin: Koin
) : Lockable() {
    private val linkedScopes: ArrayList<Scope> = arrayListOf()

    @KoinInternalApi
    var _source: Any? = null

    val closed: Boolean
        get() = _closed

    fun isNotClosed() = !closed

    private val _callbacks = arrayListOf<ScopeCallback>()

    @KoinInternalApi
    val _parameterStack = ArrayDeque<ParametersHolder>()

    private var _closed: Boolean = false
    val logger: Logger get() = _koin.logger

    internal fun create(links: List<Scope>) {
        linkedScopes.addAll(links)
    }

    /**
     * Add parent Scopes to allow instance resolution
     * i.e: linkTo(scopeC) - allow to resolve instance to current scope and scopeC
     *
     * @param scopes - Scopes to link with
     */
    fun linkTo(vararg scopes: Scope) {
        if (!isRoot) {
            linkedScopes.addAll(scopes)
        } else {
            error("Can't add scope link to a root scope")
        }
    }

    /**
     * Remove linked scope
     */
    fun unlink(vararg scopes: Scope) {
        if (!isRoot) {
            linkedScopes.removeAll(scopes)
        } else {
            error("Can't remove scope link to a root scope")
        }
    }

    /**
     * Lazy inject a Koin instance
     * @param qualifier
     * @param mode - LazyThreadSafetyMode
     * @param parameters
     *
     * @return Lazy instance of type T
     */
    inline fun <reified T : Any> inject(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
        lazy(mode) { get<T>(qualifier, parameters) }

    /**
     * Lazy inject a Koin instance if available
     * @param qualifier
     * @param mode - LazyThreadSafetyMode
     * @param parameters
     *
     * @return Lazy instance of type T or null
     */
    inline fun <reified T : Any> injectOrNull(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T?> =
        lazy(mode) { getOrNull<T>(qualifier, parameters) }

    /**
     * Get a Koin instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return get(T::class, qualifier, parameters)
    }

    /**
     * Get Koin Scope "source" instance. Retrive the object instance, that initiated the creation of the scope.
     *
     * Deprecation: Source instance resolution is now done within graph resolution part. It's done in the regular "get()" function.
     */
    @Deprecated("No need to use getSource(). You can an use get() directly.", ReplaceWith("get()"))
    inline fun <reified T : Any> getSource(): T? {
        return _source as? T
    }

    /**
     * Get a Koin instance if available
     * @param qualifier
     * @param scope
     * @param parameters
     *
     * @return instance of type T or null
     */
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
    fun <T> getOrNull(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): T? {
        return try {
            get(clazz, qualifier, parameters)
        } catch (e: ClosedScopeException) {
            _koin.logger.error("* Scope closed - no instance found for ${clazz.getFullName()} on scope ${toString()}")
            null
        } catch (e: NoBeanDefFoundException) {
            _koin.logger.error("* No instance found for ${clazz.getFullName()} on scope ${toString()}")
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
    fun <T> get(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
    ): T {
        return if (_koin.logger.isAt(Level.DEBUG)){
            val qualifierString = qualifier?.let { " with qualifier '$qualifier'" } ?: ""
            _koin.logger.display(Level.DEBUG, "|- '${clazz.getFullName()}'$qualifierString ...")

            val start = KoinPlatformTimeTools.getTimeInNanoSeconds()
            val instance = resolveInstance<T>(qualifier, clazz, parameters)
            val stop = KoinPlatformTimeTools.getTimeInNanoSeconds()
            val duration = (stop-start)/Timer.NANO_TO_MILLI

            _koin.logger.display(Level.DEBUG, "|- '${clazz.getFullName()}' in $duration ms")
            instance
        } else {
            resolveInstance(qualifier, clazz, parameters)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        parameterDef: ParametersDefinition?
    ): T {
        if (_closed) {
            throw ClosedScopeException("Scope '$id' is closed")
        }
        val parameters = parameterDef?.invoke()
        if (parameters != null) {
            _koin.logger.log(Level.DEBUG){ "| >> parameters $parameters " }
            KoinPlatformTools.synchronized(this@Scope) {
                _parameterStack.addFirst(parameters)
            }
        }
        val instanceContext = InstanceContext(_koin, this, parameters)
        val value = resolveValue<T>(qualifier, clazz, instanceContext, parameterDef)
        if (parameters != null) {
            _koin.logger.debug("| << parameters")
            KoinPlatformTools.synchronized(this@Scope) {
                _parameterStack.removeFirstOrNull()
            }
        }
        return value
    }

    private fun <T> resolveValue(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        instanceContext: InstanceContext,
        parameterDef: ParametersDefinition?
    ) = (_koin.instanceRegistry.resolveInstance(qualifier, clazz, this.scopeQualifier, instanceContext)
        ?: run {
            _koin.logger.debug("|- ? t:'${clazz.getFullName()}' - q:'$qualifier' look in injected parameters")
            _parameterStack.firstOrNull()?.getOrNull<T>(clazz)
        }
        ?: run {
            _koin.logger.debug("|- ? t:'${clazz.getFullName()}' - q:'$qualifier' look at scope source" )
            _source?.let {
                if (clazz.isInstance(it)) {
                    _source as? T
                } else null
            }
        }
        ?: run {
            _koin.logger.debug("|- ? t:'${clazz.getFullName()}' - q:'$qualifier' look in other scopes" )
            findInOtherScope<T>(clazz, qualifier, parameterDef)
        }
        ?: run {
            KoinPlatformTools.synchronized(this@Scope) {
                _parameterStack.clear()
            }
            _koin.logger.debug("|- << parameters" )
            throwDefinitionNotFound(qualifier, clazz)
        })

    @Suppress("UNCHECKED_CAST")
    private fun <T> getFromSource(clazz: KClass<*>): T? {
        return if (clazz.isInstance(_source)) _source as? T else null
    }

    private fun <T> findInOtherScope(
        clazz: KClass<*>,
        qualifier: Qualifier?,
        parameters: ParametersDefinition?
    ): T? {
        var instance: T? = null
        for (scope in linkedScopes) {
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
            "No definition found for class:'${clazz.getFullName()}' q:'$qualifierString'. Check your definitions!"
        )
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
    inline fun <reified T> declare(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true
    ) = KoinPlatformTools.synchronized(this) {
        _koin.instanceRegistry.declareScopedInstance(
            instance,
            qualifier,
            secondaryTypes,
            allowOverride,
            scopeQualifier,
            id
        )
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
    fun <T> getAll(clazz: KClass<*>): List<T> {
        val context = InstanceContext(_koin, this)
        return _koin.instanceRegistry.getAll<T>(clazz, context) + linkedScopes.flatMap { scope -> scope.getAll(clazz) }
    }

    /**
     * Retrieve a property
     * @param key
     * @param defaultValue
     */
    fun <T : Any> getProperty(key: String, defaultValue: T): T = _koin.getProperty(key, defaultValue)

    /**
     * Retrieve a property
     * @param key
     */
    fun <T : Any> getPropertyOrNull(key: String): T? = _koin.getProperty(key)

    /**
     * Retrieve a property
     * @param key
     */
    fun <T : Any> getProperty(key: String): T = _koin.getProperty(key)
        ?: throw MissingPropertyException("Property '$key' not found")

    /**
     * Close all instances from this scope
     */
    fun close() = KoinPlatformTools.synchronized(this) {
        _closed = true
        clearData()
        _koin.scopeRegistry.deleteScope(this)
    }

    private fun clearData() {
        _source = null
        _koin.logger.debug("|- (-) Scope - id:'$id'")
        // call on close from callbacks
        _callbacks.forEach { it.onScopeClose(this) }
        _callbacks.clear()
    }

    override fun toString(): String {
        return "['$id']"
    }
}

typealias ScopeID = String
