package org.koin

import org.koin.bean.BeanRegistry
import org.koin.context.Scope
import org.koin.instance.InstanceResolver
import org.koin.module.Module
import org.koin.property.PropertyResolver
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin {

    val logger: Logger = Logger.getLogger(Koin::class.java.simpleName)

    val beanRegistry = BeanRegistry()
    val propertyResolver = PropertyResolver()
    val instanceResolver = InstanceResolver()

    init {
        logger.info("(-) Koin Started ! (-)")
        instanceResolver.createContext(Scope.root())
    }

    /**
     * Inject properties to context
     */
    fun properties(props: Map<String, Any>): Koin {
        logger.info("load properties $props ...")
        propertyResolver.addAll(props)
        return this
    }

    /**
     * Load modules
     */
    fun build(vararg classes: KClass<out Module>): KoinContext {
        logger.info("load module $classes ...")

        val koinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
        classes.forEach {
            val module = it.createInstance()
            module.koinContext = koinContext
            instanceResolver.createContext(module.scope())
            module.onLoad()
        }
        return koinContext
    }
}
