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
        logger.info("(-) Koin Started ! (-)")
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
     * Load modules
     */
    fun build(vararg classes: KClass<out Module>): Context {
        logger.info("load module $classes ...")

        if (classes.isEmpty()) {
            context.import(EmptyModule::class)
        } else {
            classes.map { context.import(it) }
        }
        return context
    }
}
