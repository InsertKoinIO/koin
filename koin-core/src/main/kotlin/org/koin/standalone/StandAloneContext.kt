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

    /**
     * Koin Context
     */
    var koinContext: StandAloneKoinContext = BlankContext()

    /**
     * Koin starter
     */
    fun startKoin(list: List<Module>, bindSystemProperties: Boolean = false, properties: Map<String, Any> = HashMap()) {

        if (koinContext.isStarted()) {
            throw AlreadyStartedException()
        }

        val koin = if (bindSystemProperties) {
            // Koin properties will override system properties
            Koin().bindKoinProperties().bindAdditionalProperties(properties).bindSystemProperties()
        } else {
            Koin().bindKoinProperties().bindAdditionalProperties(properties)
        }

        // Build koin context
        koinContext = koin.build(list)
    }

    /**
     * Close actual Koin context
     */
    fun closeKoin() {
        if (koinContext.isStarted()) {
            // Close all
            (koinContext as KoinContext).close()
            // blank object
            koinContext = BlankContext()
        }
    }
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext {
    /**
     * Is Koin started ?
     */
    fun isStarted(): Boolean
}

/**
 * Blank Stand Alone Context
 */
private class BlankContext : StandAloneKoinContext {
    override fun isStarted() = false
}