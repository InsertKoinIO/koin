package org.koin.spark

import org.koin.core.bean.Definition
import org.koin.dsl.context.Context

/**
 * Declare a Spark controller
 */
inline fun <reified T : Any> Context.controller(name: String = "", noinline definition: Definition<T>) {
    val def = bean(name, definition)
    def.bind(SparkController::class)
}

/**
 * Tag interface for Spark controllers
 */
internal interface SparkController