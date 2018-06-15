package org.koin.spark

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition

/**
 * Declare a Spark controller
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : Any> ModuleDefinition.controller(
    name: String = "",
    override: Boolean = false,
    noinline definition: Definition<T>
) {
    val def = single(name, true, override, definition)
    def.bind(SparkController::class)
}

/**
 * Tag interface for Spark controllers
 */
internal interface SparkController