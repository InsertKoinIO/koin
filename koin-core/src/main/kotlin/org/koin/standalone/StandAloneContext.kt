package org.koin.standalone

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module

/**
 * Koin registry
 */
object StandAloneContext {
    /**
     * Koin Context
     */
    lateinit var koinContext: KoinContext
}

/**
 * releaseContext any context
 */
fun releaseContext(name: String = "") = StandAloneContext.koinContext.release(name)

/**
 * Koin Context builder
 */
fun startContext(list: List<Module>) {
    StandAloneContext.koinContext = Koin().build(list)
}

/**
 * Koin Context builder
 */
fun startContext(vararg list: Module) {
    StandAloneContext.koinContext = Koin().build(*list)
}