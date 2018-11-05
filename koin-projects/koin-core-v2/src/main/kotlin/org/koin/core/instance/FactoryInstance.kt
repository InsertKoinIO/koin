package org.koin.core.instance

import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition

class FactoryInstance<T>(private val beanDefinition: BeanDefinition<T>) :
    Instance<T> {

    var value: T? = null

    override fun release() {}

    override fun isAlreadyCreated(): Boolean = false

    override fun <T> get(parameters: ParametersDefinition?): T {
        return create(beanDefinition, parameters)
    }
}