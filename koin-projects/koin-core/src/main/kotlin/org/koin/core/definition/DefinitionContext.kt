package org.koin.core.definition

import org.koin.core.Koin
import org.koin.core.error.MissingPropertyException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeInstance

sealed class DefinitionContext(val koin: Koin) {

    abstract fun currentScope(): ScopeInstance

    /**
     * Resolve an instance from Koin
     * @param qualifier
     * @param parameters
     */
    inline fun <reified T> get(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(qualifier, currentScope(), parameters)
    }

    /**
     * Resolve an instance from Koin / extenral scope instance
     * @param qualifier
     * @param scope
     * @param parameters
     */
    inline fun <reified T> get(
            qualifier: Qualifier? = null,
            scope: ScopeInstance,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(qualifier, scope, parameters)
    }

    inline fun <reified T> getFromScope(
            scopeId: String,
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(qualifier, koin.getScope(scopeId), parameters)
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