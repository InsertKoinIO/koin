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

class Koin {

    val beanRegistry = BeanRegistry()
    val instanceResolver = InstanceResolver()
    val scopeRegistry = ScopeRegistry()

    inline fun <reified T> inject(name: String? = null, noinline parameters: ParametersDefinition? = null): Lazy<T> =
        lazy { get<T>(name, parameters) }

    inline fun <reified T> get(name: String? = null, noinline parameters: ParametersDefinition? = null): T =
        synchronized(this) {
            val clazz = T::class
            return logDuration("[Koin] got '$clazz'") {

                val definition: BeanDefinition<*> = beanRegistry.findDefinition(name, clazz)
                        ?: throw NoDefinitionFoundException("No definition for '$clazz' has been found. Check your module definitions.")

                checkScope(definition)
                instanceResolver.resolveInstance(definition, parameters)
            }
        }

    fun checkScope(definition: BeanDefinition<*>) {
        if (definition.isScoped()) {
            val scopeId = definition.getScopeId() ?: error("No scope id registered on $definition")
            scopeRegistry.getScope(scopeId)
                    ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created while trying to use scoped definition: $definition")
        }
    }

    fun createEagerInstances() {
        KoinApplication.log("[Koin] creating instances at start ...")
        return logDuration("[Koin] created instances at start") {
            val definitions: List<BeanDefinition<*>> = beanRegistry.findAllCreatedAtStartDefinition()
            definitions.forEach {
                instanceResolver.resolveInstance(it, null)
            }
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

    private fun linkScope(createdScope: Scope) {
        createdScope.koin = this
    }

    fun getOrCreateScope(scopeId: String): Scope {
        val scope = scopeRegistry.getOrCreateScope(scopeId)
        if (!scope.isRegistered()) {
            scope.register(this)
        }
        return scope
    }

    internal fun closeScope(id: String) {
        beanRegistry.releaseInstanceForScope(id)
        scopeRegistry.deleteScope(id)
    }
}