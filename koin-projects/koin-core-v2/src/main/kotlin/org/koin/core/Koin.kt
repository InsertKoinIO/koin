package org.koin.core

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.registry.BeanRegistry
import org.koin.core.time.logDuration

class Koin {

    val beanRegistry = BeanRegistry()

    inline fun <reified T> get(name: String? = null): T {
        val clazz = T::class
        return logDuration("[Koin] get '$clazz'") {

            val definition: BeanDefinition<*>? =
                name?.let { beanRegistry.findDefinitionByName(name) } ?: beanRegistry.findDefinitionByClass(clazz)

            definition?.let {
                definition.instance.get<T>()
            }
                    ?: throw NoBeanDefFoundException("No definition for '$clazz' has been found. Check your module definitions.")
        }
    }
}