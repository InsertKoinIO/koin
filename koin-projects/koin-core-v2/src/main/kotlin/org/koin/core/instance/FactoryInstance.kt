package org.koin.core.instance

import org.koin.core.bean.BeanDefinition

class FactoryInstance<T>(val beanDefinition: BeanDefinition<T>) :
    Instance<T> {

    var value: T? = null

    override fun release() {}

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(): T {
        return create(beanDefinition)
    }
}