package org.koin.core

import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.registry.BeanRegistry
import org.koin.core.registry.InstanceResolver
import org.koin.core.time.logDuration

class Koin {

    val beanRegistry = BeanRegistry()
    val instanceResolver = InstanceResolver()

    inline fun <reified T> inject(name: String? = null, noinline parameters: ParametersDefinition? = null): Lazy<T> =
        lazy { get<T>(name, parameters) }

    inline fun <reified T> get(name: String? = null, noinline parameters: ParametersDefinition? = null): T =
        synchronized(this) {
            val clazz = T::class
            return logDuration("[Koin] got '$clazz'") {
                val definition: BeanDefinition<*>? = beanRegistry.findDefinition(name, clazz)
                instanceResolver.resolveInstance(definition, parameters, clazz)
            }
        }

    fun createEagerInstances() {
        KoinApplication.log("[Koin] creating instances at start ...")
        return logDuration("[Koin] created instances at start") {
            val definitions: List<BeanDefinition<*>> = beanRegistry.findAllCreatedAtStartDefinition()
            definitions.forEach {
                instanceResolver.resolveInstance(it, null, it.primaryType)
            }
        }
    }

    fun close() {
        beanRegistry.close()
        instanceResolver.close()
    }
}