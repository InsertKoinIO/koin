package org.koin.standalone

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