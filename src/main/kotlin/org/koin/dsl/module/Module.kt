package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.dsl.context.Context


/**
 * Module class - Help define beans within actual context
 * @author - Arnaud GIULIANI
 */
abstract class Module {

    lateinit var koinContet: KoinContext

    /**
     * context module definition
     * use function declareContext - to declare your context
     */
    abstract fun context(): Context

    /**
     * Help create context
     */
    fun declareContext(init: Context.() -> Unit) = Context(koinContet).apply(init)

}
