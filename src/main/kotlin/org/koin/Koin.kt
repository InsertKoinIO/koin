package org.koin

import org.koin.module.EmptyModule
import org.koin.module.Module
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin() {

    val logger: Logger = Logger.getLogger(Koin::class.java.simpleName)

    val context: Context = Context()

    init {
        logger.info("Koin Start !")
    }

    /**
     * Inject properties to context
     */
    fun properties(props: Map<String, Any>): Koin {
        logger.info("load properties $props ...")

        context.propertyResolver.properties += props
        return this
    }

    /**
     * Load module
     */
    fun build(clazz: KClass<out Module>? = null): Context {
        logger.info("load module ${clazz?.simpleName} ...")

        val module: KClass<out Module>
        if (clazz == null) {
            module = EmptyModule::class
        } else {
            module = clazz
        }
        context.import(module)
        return context
    }
}
