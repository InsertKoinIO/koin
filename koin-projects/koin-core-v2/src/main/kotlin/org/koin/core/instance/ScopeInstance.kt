package org.koin.core.instance

import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope

class ScopeInstance<T>(private val beanDefinition: BeanDefinition<T>) : Instance<T> {

    override fun isCreated(scope: Scope?): Boolean = scope?.let { values[scope.internalId] != null } ?: false

    private val values: HashMap<String, T> = hashMapOf()

    override fun release(scope: Scope?) {
        scope?.let {
            KoinApplication.log("[Koin] releasing '$scope' ~ $beanDefinition ")
            values.remove(scope.internalId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(scope: Scope?, parameters: ParametersDefinition?): T {
        if (scope == null) error("Scope should not be null for ScopeInstance")

        val internalId = scope.internalId
        var current = values[internalId]
        if (current == null) {
            current = create(beanDefinition, parameters)
            values[internalId] = current ?:
                    throw InstanceCreationException("Instance creation from $beanDefinition should not be null")
        }
        return current as T
    }
}