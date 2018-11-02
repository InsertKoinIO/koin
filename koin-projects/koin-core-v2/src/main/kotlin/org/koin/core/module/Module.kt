package org.koin.core.module

import org.koin.core.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.bean.FactoryInstance
import org.koin.core.bean.SingleInstance

class Module() {
    val definitions = hashSetOf<BeanDefinition<*>>()
    lateinit var koin: Koin

    inline fun <reified T> single(name: String? = null, noinline definition: Definition<T>) {
        declareDefinition(createSingle(name, definition))
    }

    inline fun <reified T> factory(name: String? = null, noinline definition: Definition<T>) {
        declareDefinition(createFactory(name, definition))
    }

    fun <T> declareDefinition(definition: BeanDefinition<T>) {
        definitions.add(definition)
    }

    inline fun <reified T> get(): T {
        return koin.get()
    }
}

inline fun <reified T> createSingle(name: String? = null, noinline definition: Definition<T>): BeanDefinition<T> {
    val beanDefinition = BeanDefinition(name, T::class, definition)
    beanDefinition.instance = SingleInstance(beanDefinition)
    return beanDefinition
}

inline fun <reified T> createFactory(name: String? = null, noinline definition: Definition<T>): BeanDefinition<T> {
    val beanDefinition = BeanDefinition(name, T::class, definition)
    beanDefinition.instance = FactoryInstance(beanDefinition)
    return beanDefinition
}