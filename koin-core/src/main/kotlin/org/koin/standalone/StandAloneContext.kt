package org.koin.standalone

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module
import org.koin.error.AlreadyStartedException

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {

    private var isStarted = false

    /**
     * Koin Context
     */
    lateinit var koinContext: StandAloneKoinContext

    /**
     * Koin starter
     */
    fun startKoin(list: List<Module>, useEnvironmentProperties: Boolean = false, properties: Map<String, Any> = HashMap()): Koin = synchronized(this) {
        if (isStarted) {
            throw AlreadyStartedException()
        }

        val koin = if (useEnvironmentProperties) {
            // Koin properties will override system properties
            Koin().bindKoinProperties().bindAdditionalProperties(properties).bindEnvironmentProperties()
        } else {
            Koin().bindKoinProperties().bindAdditionalProperties(properties)
        }

        // Build koin context
        val build = koin.build(list)
        isStarted = true
        return build
    }

    /**
     * Close actual Koin context
     */
    fun closeKoin() = synchronized(this) {
        if (isStarted) {
            // Close all
            (koinContext as KoinContext).close()
            isStarted = false
        }
    }
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext