package org.koin.core.definition

import org.koin.core.Koin
import org.koin.core.error.MissingPropertyException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

sealed class DefinitionContext(val koin: Koin) {

    abstract fun currentScope(): ScopeInstance

    /**
     * Resolve an instance from Koin
     * @param name
     * @param parameters
     */
    inline fun <reified T> get(
            name: String? = null,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(name, currentScope(), parameters)
    }

    /**
     * Resolve an instance from Koin / extenral scope instance
     * @param name
     * @param scope
     * @param parameters
     */
    inline fun <reified T> get(
            name: String? = null,
            scope: ScopeInstance,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(name, scope, parameters)
    }

    inline fun <reified T> getFromScope(
            scopeId: String,
            name: String? = null,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(name, koin.getScope(scopeId), parameters)
    }

    /**
     * Get a property from Koin
     * @param key
     * @param defaultValue
     */
    fun <T> getProperty(key: String, defaultValue: T? = null): T {
        return koin.getProperty(key, defaultValue)
                ?: throw MissingPropertyException("Property '$key' is missing")
    }
}

class DefaultContext(koin: Koin) : DefinitionContext(koin) {
    override fun currentScope(): ScopeInstance = ScopeInstance.GLOBAL
}

class ScopedContext(koin: Koin, private val scopeInstance: ScopeInstance) : DefinitionContext(koin) {
    override fun currentScope(): ScopeInstance = scopeInstance
}