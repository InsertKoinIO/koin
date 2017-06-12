package org.koin.module

import org.koin.Context


/**
 * Module class - Help define beans within actual context
 * @author - Arnaud GIULIANI
 */
abstract class Module() {

    lateinit var context: Context

    /**
     * onLoad module definition
     * use function declareContext - to declare your context
     */
    abstract fun onLoad()

    /**
     * Help declare beans into current context
     */
    fun declareContext(definition: Context.() -> Unit) = definition(context)
}