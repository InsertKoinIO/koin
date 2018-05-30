package org.koin.spark

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.parameter.emptyParameterDefinition

/**
 * Run all Spark controllers
 */
fun KoinContext.runSparkControllers() {
    Koin.logger.log("** Run Spark Controllers **")
    beanRegistry.definitions
        .filter { it.types.contains(SparkController::class) }
        .forEach { def ->
            Koin.logger.log("Creating $def ...")
            instanceFactory.retrieveInstance<Any>(def, emptyParameterDefinition())
        }
}