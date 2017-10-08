package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.dsl.context.Context
import kotlin.reflect.KClass


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
    fun newContext(scope: KClass<*>? = null, init: Context.() -> Unit) = Context(scope, koinContext).apply(init)
}
