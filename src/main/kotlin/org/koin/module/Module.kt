package org.koin.module

import org.koin.KoinContext
import org.koin.context.Context
import org.koin.context.Scope
import kotlin.reflect.KClass


/**
 * Module class - Help define beans within actual context
 * @author - Arnaud GIULIANI
 */
abstract class Module(var scope: KClass<*>? = null) {

    lateinit var koinContext: KoinContext

    /**
     * onLoad module definition
     * use function declareContext - to declare your context
     */
    abstract fun onLoad()

    /**
     * Handle scope for Context
     */
    fun scope() = Scope(scope)

    /**
     * Help declare beans into current context
     */
    fun declareContext(definition: Context.() -> Unit) {
        definition(Context(koinContext.beanRegistry, koinContext.propertyResolver, koinContext.instanceResolver, this))
    }
}