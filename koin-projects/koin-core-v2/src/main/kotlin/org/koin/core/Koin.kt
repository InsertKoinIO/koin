package org.koin.core

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.InstanceResolver
import org.koin.core.registry.ScopeRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeId
import org.koin.core.time.logDuration
import kotlin.reflect.KClass

class Koin {

    val beanRegistry = BeanRegistry()
    val instanceResolver = InstanceResolver()
    val scopeRegistry = ScopeRegistry()

    inline fun <reified T> inject(
        name: String? = null,
        scope: Scope? = null,
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> =
        lazy { get<T>(name, scope, parameters) }

    inline fun <reified T> get(
        name: String? = null,
        scope: Scope? = null,
        noinline parameters: ParametersDefinition? = null
    ): T =
        synchronized(this) {
            val clazz = T::class
            return logDuration("[Koin] got '$clazz'") {
                get(clazz, name, scope, parameters)
            }
        }

    inline fun <reified T> get(
        clazz: KClass<*>,
        name: String?,
        scope: Scope?,
        noinline parameters: ParametersDefinition?
    ): T {
        val definition: BeanDefinition<*> = beanRegistry.findDefinition(name, clazz)
                ?: throw NoDefinitionFoundException("No definition for '$clazz' has been found. Check your module definitions.")

        val targetScope = prepareScope(definition, scope)

        return instanceResolver.resolveInstance(definition, targetScope, parameters) as T
    }

    fun prepareScope(definition: BeanDefinition<*>, scope: Scope? = null): Scope? {
        return if (definition.isScoped()) {
            if (scope == null) {
                val scopeId = definition.getScopeId() ?: error("No scope id registered on $definition")
                scopeRegistry.getScopeById(scopeId)
                        ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created while trying to use scoped definition: $definition")
            } else scope
        } else null
    }

    internal fun createEagerInstances() {
        val definitions: List<BeanDefinition<*>> = beanRegistry.findAllCreatedAtStartDefinition()
        definitions.forEach {
            instanceResolver.resolveInstance(it, null, null)
        }
    }

    fun close() {
        beanRegistry.close()
        instanceResolver.close()
    }

    fun createScope(scopeId: String): Scope {
        val createdScope = scopeRegistry.createScope(scopeId)
        createdScope.register(this)
        return createdScope
    }

    fun getOrCreateScope(scopeId: String): Scope {
        val scope = scopeRegistry.getOrCreateScope(scopeId)
        if (!scope.isRegistered()) {
            scope.register(this)
        }
        return scope
    }

    fun detachScope(scopeId: String): Scope {
        val createdScope = scopeRegistry.detachScope(scopeId)
        createdScope.register(this)
        return createdScope
    }

    fun getDetachedScope(internalId: String): Scope? {
        return scopeRegistry.getScopeByInternalId(internalId)
    }

    internal fun closeScope(internalId: String) {
        val scope: Scope = scopeRegistry.getScopeByInternalId(internalId) ?: error("Scope not found '$internalId'")
        beanRegistry.releaseInstanceForScope(scope)
        scopeRegistry.deleteScope(scope)
    }

}