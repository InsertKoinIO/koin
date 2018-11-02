package org.koin.core

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.registry.BeanRegistry
import org.koin.core.time.logDuration
import kotlin.reflect.KClass

class Koin {

    val beanRegistry = BeanRegistry()

    inline fun <reified T> inject(name: String? = null): Lazy<T> = lazy { get<T>(name) }

    inline fun <reified T> get(name: String? = null): T {
        val clazz = T::class
        return logDuration("[Koin] get '$clazz'") {
            val definition: BeanDefinition<*>? = findDefinition(name, clazz)
            resolveInstance(definition, clazz)
        }
    }

    inline fun <reified T> resolveInstance(
        definition: BeanDefinition<*>?,
        clazz: KClass<*>
    ): T {
        return definition?.instance?.get<T>()
                ?: throw NoDefinitionFoundException("No definition for '$clazz' has been found. Check your module definitions.")
    }

    fun findDefinition(
        name: String?,
        clazz: KClass<*>
    ): BeanDefinition<*>? =
        name?.let { beanRegistry.findDefinitionByName(name) } ?: beanRegistry.findDefinitionByClass(clazz)
}