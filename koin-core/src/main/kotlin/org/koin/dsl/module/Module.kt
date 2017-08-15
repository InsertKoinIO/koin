package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.dsl.context.Context


/**
 * Module class - Help define beans within actual context
 * @author - Arnaud GIULIANI
 */
abstract class Module {

    lateinit var koinContext: KoinContext

    /**
     * module's context
     */
    abstract fun context(): Context

    /**
     * Create Context function
     */
    fun declareContext(init: Context.() -> Unit) = Context(koinContext).apply(init)

}
