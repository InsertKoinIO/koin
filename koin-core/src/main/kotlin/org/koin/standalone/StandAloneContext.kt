package org.koin.standalone

import org.koin.KoinContext

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
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