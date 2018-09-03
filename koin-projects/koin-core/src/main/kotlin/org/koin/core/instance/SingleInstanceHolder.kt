package org.koin.core.instance

import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition

/**
 * Single - InstanceHolder
 * create a unique instance
 */
class SingleInstanceHolder<T : Any>(override val bean: BeanDefinition<T>) : InstanceHolder<T> {

    lateinit var instance: T

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameters: ParameterDefinition): Instance<T> {
        val needCreation = (!this::instance.isInitialized)
        if (needCreation) {
            instance = create(parameters)
        }
        return Instance(instance as T, needCreation)
    }

    override fun release() {}
}