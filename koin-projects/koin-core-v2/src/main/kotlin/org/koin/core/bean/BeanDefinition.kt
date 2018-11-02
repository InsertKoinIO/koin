package org.koin.core.bean

import kotlin.reflect.KClass

//TODO Options & Attributes
data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>
) {
    lateinit var definition: Definition<T>
    lateinit var instance: Instance<T>

    companion object {
        inline fun <reified T> createSingle(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition)
            beanDefinition.instance = SingleInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createFactory(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition)
            beanDefinition.instance = FactoryInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createDefinition(
            name: String?,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = BeanDefinition<T>(name, T::class)
            beanDefinition.definition = definition
            return beanDefinition
        }
    }
}

typealias Definition<T> = () -> T