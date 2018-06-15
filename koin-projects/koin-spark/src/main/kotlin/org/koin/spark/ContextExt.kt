package org.koin.spark

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition

/**
 * Declare a Spark controller
 */
inline fun <reified T : Any> ModuleDefinition.controller(name: String = "", noinline definition: Definition<T>) {
    val def = single(name, true, false, definition)
    def.bind(SparkController::class)
}

/**
 * Tag interface for Spark controllers
 */
internal interface SparkController