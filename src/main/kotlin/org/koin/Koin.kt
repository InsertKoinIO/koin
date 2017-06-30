package org.koin

import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.dsl.module.Module
import org.koin.instance.InstanceResolver
import org.koin.property.PropertyResolver
import java.util.logging.Logger

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
    fun <T : Module> build(vararg modules: T): KoinContext {
        logger.info("load module $modules ...")

        val koinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
        modules.forEach {
            it.koinContet = koinContext
            val ctx = it.context()
            val scope = ctx.contextScope
            if (scope != null) {
                logger.info("preparing scope $scope")
                instanceResolver.createContext(scope)
            }
        }

        return koinContext
    }

    fun build(): KoinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
}
