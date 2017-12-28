package org.koin.sample.util

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.context.Context

/**
 * Declare a Spark controller
 */
inline fun <reified T : Any> Context.controller(name: String = "", noinline definition: () -> T) {
    val def = provide(name, true, definition)
    def.bind(SparkController::class)
}

/**
 * Tag interface for Spark controllers
 */
internal interface SparkController


/**
 * Run all declared Spark controllers
 */
fun runControllers() {
    (org.koin.standalone.StandAloneContext.koinContext as KoinContext).runSparkControllers()
}

/**
 * Run all declared Spark controllers
 */
fun KoinContext.runSparkControllers() {
    Koin.logger.log("** Run Spark Controllers **")
    beanRegistry.definitions.keys
            .filter { it.bindTypes.contains(SparkController::class) }
            .forEach { def ->
                Koin.logger.log("Creating $def ...")
                instanceFactory.retrieveInstance<Any>(def)
            }
}