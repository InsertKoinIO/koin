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

    inline fun <reified T> get(name: String? = null): T = synchronized(this){
        val clazz = T::class
        return logDuration("[Koin] got '$clazz'") {
            val definition: BeanDefinition<*>? = beanRegistry.findDefinition(name, clazz)
            instanceResolver.resolveInstance(definition, clazz)
        }
    }

    fun createEagerInstances() {
        KoinApplication.log("[Koin] creating instances at start ...")
        return logDuration("[Koin] created instances at start") {
            val definitions: List<BeanDefinition<*>> = beanRegistry.findAllCreatedAtStartDefinition()
            definitions.forEach {
                instanceResolver.resolveInstance(it,it.primaryType)
            }
        }
    }

    fun close() {
        beanRegistry.close()
        instanceResolver.close()
    }
}