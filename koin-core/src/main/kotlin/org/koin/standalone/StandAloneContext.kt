package org.koin.standalone

import org.koin.Koin
import org.koin.dsl.module.Module

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {
    /**
     * Koin Context
     */
    lateinit var koinContext: StandAloneKoinContext
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext

/**
 * Koin starter
 */
fun startContext(list: List<Module>) {
    StandAloneContext.koinContext = Koin().build(list)
}