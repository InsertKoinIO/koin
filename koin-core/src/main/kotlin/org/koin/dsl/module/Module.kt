package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.core.scope.Scope
import org.koin.dsl.context.Context


/**
 * Module  - Help define beans within actual context
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
    fun applicationContext(init: Context.() -> Unit) = Context(Scope.ROOT, koinContext).apply(init)
}
