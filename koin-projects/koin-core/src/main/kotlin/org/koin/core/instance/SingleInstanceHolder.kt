package org.koin.core.instance

import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition

/**
 * Single - InstanceHolder
 * create a unique instance
 */
class SingleInstanceHolder<T>(override val bean: BeanDefinition<T>) : InstanceHolder<T> {

    private var instance: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameters: ParameterDefinition): Instance<T> {
        val needCreation= (instance == null)
        if (needCreation){
            instance = create(parameters)
        }
        return Instance(instance as T, needCreation)
    }

    override fun release() {
        instance = null
    }
}