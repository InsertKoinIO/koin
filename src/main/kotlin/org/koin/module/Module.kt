package org.koin.module

import org.koin.KoinContext
import org.koin.context.Context
import org.koin.context.Scope


/**
 * Module class - Help define beans within actual context
 * @author - Arnaud GIULIANI
 */
abstract class Module() {

    lateinit var koinContext: KoinContext
    var scope: Scope = Scope.root()

    /**
     * onLoad module definition
     * use function declareContext - to declare your context
     */
    abstract fun onLoad()

    /**
     * Help declare beans into current context
     */
    fun Context(definition: Context.() -> Unit) {
        definition(Context(koinContext.beanRegistry, koinContext.propertyResolver, koinContext.instanceResolver, this))
    }

    /**
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> scope(noinline definition: () -> T) {
        scope = Scope.fromClass(definition()::class)
    }
}