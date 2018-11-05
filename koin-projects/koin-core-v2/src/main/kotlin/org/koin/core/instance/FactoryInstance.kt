package org.koin.core.instance

import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition

class FactoryInstance<T>(val beanDefinition: BeanDefinition<T>) :
    Instance<T> {

    var value: T? = null

    override fun release() {}

    override fun isAlreadyCreated(): Boolean = false

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameters: ParametersDefinition?): T {
        return create(beanDefinition, parameters)
    }
}