package org.koin.core.instance.holder

import org.koin.core.Koin
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.scope.Scope
import org.koin.dsl.definition.BeanDefinition

/**
 * Scope - InstanceHolder
 * create a unique instance
 */
class ScopeInstanceHolder<T>(override val bean: BeanDefinition<T>, val scope: Scope) :
    InstanceHolder<T> {

    private var instance: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameters: ParameterDefinition): Instance<T> {
        val needCreation = (instance == null)
        if (needCreation) {
            instance = create(parameters)
        }
        Koin.logger.debug("[Scope] get '${bean.primaryType}' from $scope")
        return Instance(instance as T, needCreation)
    }

    override fun release() {
        instance = null
    }
}