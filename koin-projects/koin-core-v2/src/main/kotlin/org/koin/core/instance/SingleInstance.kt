package org.koin.core.instance

import org.koin.core.bean.BeanDefinition

class SingleInstance<T>(val beanDefinition: BeanDefinition<T>) : Instance<T> {

    override fun isAlreadyCreated(): Boolean = (value != null)

    var value: T? = null

    override fun release() {}

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(): T {
        if (value == null) {
            value = create(beanDefinition)
        }
        return value as? T ?: error("Single instance created couldn't return value")
    }
}