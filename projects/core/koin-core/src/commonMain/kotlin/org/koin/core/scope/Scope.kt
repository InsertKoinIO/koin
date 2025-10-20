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
package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.ClosedScopeException
import org.koin.core.error.MissingPropertyException
import org.koin.core.error.MissingScopeValueException
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.instance.ResolutionContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.module.KoinDslMarker
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.resolution.InstanceResolver
import org.koin.core.time.inMs
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatformTools
import org.koin.mp.Lockable
import org.koin.mp.ThreadLocal
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.measureTimedValue

@Suppress("UNCHECKED_CAST")
@OptIn(KoinInternalApi::class)
@KoinDslMarker
class Scope(
    val scopeQualifier: Qualifier,
    val id: ScopeID,
    val isRoot: Boolean = false,
    val scopeArchetype : TypeQualifier? = null,
    @PublishedApi
    internal val _koin: Koin,
) : Lockable() {

    internal val linkedScopes = ArrayList<Scope>()
    @KoinInternalApi
    fun getLinkedScopeIds(): List<ScopeID> = linkedScopes.map { it.id }

    @KoinInternalApi
    var sourceValue: Any? = null

    val closed: Boolean
        get() = _closed

    inline fun isNotClosed() = !closed

    private val _callbacks = LinkedHashSet<ScopeCallback>()

    @KoinInternalApi
    internal var parameterStack: ThreadLocal<ArrayDeque<ParametersHolder>>? = null

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
            linkedScopes.addAll(0, scopes.toList())
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
        noinline parameters: ParametersDefinition? = null,
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
        noinline parameters: ParametersDefinition? = null,
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
        noinline parameters: ParametersDefinition? = null,
    ): T {
        return get(T::class, qualifier, parameters)
    }

    /**
     * Get Koin Scope "source" instance. Retrive the object instance, that initiated the creation of the scope.
     *
     * Deprecation: Source instance resolution is now done within graph resolution part. It's done in the regular "get()" function.
     */
    inline fun <reified T : Any> getSource(): T? {
        return sourceValue as? T
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
        noinline parameters: ParametersDefinition? = null,
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
        parameters: ParametersDefinition? = null,
    ): T? {
        return try {
            get(clazz, qualifier, parameters)
        } catch (e: ClosedScopeException) {
            _koin.logger.debug("* Scope closed - no instance found for ${clazz.getFullName()} on scope ${toString()}")
            null
        } catch (e: NoDefinitionFoundException) {
            _koin.logger.debug("* No instance found for type '${clazz.getFullName()}' on scope '${toString()}'")
            null
        } catch (e: MissingScopeValueException) {
            _koin.logger.debug("* No Scoped value found for type '${clazz.getFullName()}' on scope '${toString()}'")
            null
        }
    }

    internal fun <T> getOrNull(
        ctx: ResolutionContext
    ): T? {
        return try {
            getWithParameters(ctx.clazz, ctx.qualifier, ctx.parameters)
        } catch (e: ClosedScopeException) {
            _koin.logger.debug("* Scope closed - no instance found for ${ctx.clazz.getFullName()} on scope ${toString()}")
            null
        } catch (e: NoDefinitionFoundException) {
            _koin.logger.debug("* No instance found for type '${ctx.clazz.getFullName()}' on scope '${toString()}'")
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
        parameters: ParametersDefinition? = null,
    ): T {
        return resolve(clazz, qualifier,parameters?.invoke())
    }

    @KoinInternalApi
    fun <T> getWithParameters(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersHolder? = null,
    ): T {
        return resolve(clazz, qualifier, parameters)
    }

    private fun <T> resolve(
        clazz: KClass<*>,
        qualifier: Qualifier?,
        parameters: ParametersHolder? = null
    ): T {
        if (!_koin.logger.isAt(Level.DEBUG)) {
            return resolveInstance(qualifier, clazz, parameters)
        }

        logInstanceRequest(clazz, qualifier)
        val result = measureTimedValue { resolveInstance<T>(qualifier, clazz, parameters) }
        logInstanceDuration(clazz, result.duration)

        return result.value
    }

    private inline fun logInstanceRequest(clazz: KClass<*>, qualifier: Qualifier?) {
        val qualifierString = qualifier?.let { " with qualifier '$qualifier'" } ?: ""
        val scopeId = if (isRoot) "" else " - scope:'$id'"
        _koin.logger.display(Level.DEBUG, "|- '${clazz.getFullName()}'$qualifierString$scopeId...")
    }

    private inline fun logInstanceDuration(clazz: KClass<*>, duration: Duration) {
        _koin.logger.display(Level.DEBUG, "|- '${clazz.getFullName()}' in ${duration.inMs} ms")
    }

    private fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        parameters: ParametersHolder?,
    ): T {
        checkScopeIsOpen()
        val instanceContext = ResolutionContext(_koin.logger, this, clazz, qualifier, parameters)
        return stackParametersCall(parameters, instanceContext)
    }

    private inline fun checkScopeIsOpen() {
        if (_closed) {
            throw ClosedScopeException("Scope '$id' is closed")
        }
    }

    private fun <T> stackParametersCall(
        parameters: ParametersHolder?,
        instanceContext: ResolutionContext
    ): T {
        if (parameters == null) {
            return resolveFromContext(instanceContext)
        }

        // stack parameters
        _koin.logger.log(Level.DEBUG) { "| >> parameters $parameters" }
        val stack = onParameterOnStack(parameters)

        try {
            return resolveFromContext(instanceContext)
        } finally {
            _koin.logger.debug("| << parameters")
            // unstack parameters
            clearParameterStack(stack)
        }
    }

    private fun onParameterOnStack(parameters: ParametersHolder): ArrayDeque<ParametersHolder> {
        val stack = getOrCreateParameterStack()
        stack.addFirst(parameters)
        return stack
    }

    private fun clearParameterStack(stack: ArrayDeque<ParametersHolder>) {
        stack.removeFirstOrNull()
        if (stack.isEmpty()) {
            parameterStack?.remove()
            parameterStack = null
        }
    }

    private fun getOrCreateParameterStack(): ArrayDeque<ParametersHolder> {
        return parameterStack?.get() ?: ArrayDeque<ParametersHolder>().let { parameterStack = ThreadLocal(); parameterStack?.set(it) ; it }
    }

    private fun <T> resolveFromContext(
        instanceContext: ResolutionContext
    ): T {
        return _koin.resolver.resolveFromContext(this,instanceContext)
    }


//    @Suppress("UNCHECKED_CAST")
//    private fun <T> getFromSource(clazz: KClass<*>): T? {
//        return if (clazz.isInstance(_source)) _source as? T else null
//    }

    /**
     * Declare an instance definition for the current scope using the given object.
     *
     * This results in declaring a scoped definition of type `T`, bound to the provided instance.
     * The instance will be dropped when the scope is closed.
     *
     * The `holdInstance` parameter controls whether the instance is retained by Koin or not:
     * - `holdInstance = true` → the instance is held within the scope and behaves like a singleton.
     * - `holdInstance = false` → the instance is not held; the definition exists in current scope, but cannot be resolved in other scope instance.
     *
     * This is useful for injecting pre-constructed instances into a specific scope.
     *
     * @param instance The instance to declare.
     * @param qualifier An optional qualifier to distinguish this binding.
     * @param secondaryTypes A list of additional types this instance should be bound to.
     * @param allowOverride Whether this declaration can override an existing one (default is true).
     * @param holdInstance Whether to retain the instance for future resolution within new scopes, or just hold for current scope (holdInstance = false).
     */
    inline fun <reified T> declare(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true,
        holdInstance : Boolean = false
    ) = KoinPlatformTools.synchronized(this) {
        _koin.instanceRegistry.scopeDeclaredInstance(
            instance,
            scopeQualifier,
            id,
            qualifier,
            secondaryTypes,
            allowOverride,
            holdInstance = holdInstance
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
        val context = ResolutionContext(_koin.logger, this, clazz)
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
        _koin.logger.debug("|- (-) Scope - id:'$id'")

        _callbacks.forEach { it.onScopeClose(this) }
        _callbacks.clear()
        _closed = true

        sourceValue = null

        parameterStack?.get()?.clear()
        parameterStack = null

        _koin.scopeRegistry.deleteScope(this)
    }

    override fun toString(): String {
        return "['$id']"
    }
}

typealias ScopeID = String
