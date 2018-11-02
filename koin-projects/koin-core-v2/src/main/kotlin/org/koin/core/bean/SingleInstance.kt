package org.koin.core.bean

class SingleInstance<T>(val beanDefinition: BeanDefinition<T>) : Instance<T> {

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