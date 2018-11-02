package org.koin.core

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.InstanceResolver
import org.koin.core.time.logDuration
import java.util.*
import kotlin.reflect.KClass

class Koin {

    val beanRegistry = BeanRegistry()
    val instanceResolver = InstanceResolver()

    inline fun <reified T> inject(name: String? = null): Lazy<T> = lazy { get<T>(name) }

    inline fun <reified T> get(name: String? = null): T {
        val clazz = T::class
        return logDuration("[Koin] get '$clazz'") {
            val definition: BeanDefinition<*>? = beanRegistry.findDefinition(name, clazz)
            instanceResolver.resolveInstance(definition, clazz)
        }
    }
}