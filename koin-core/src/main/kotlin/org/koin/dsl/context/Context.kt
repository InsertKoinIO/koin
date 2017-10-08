package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import kotlin.reflect.KClass

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val scope: KClass<*>? = null, val koinContext: KoinContext) {

    val definitions = arrayListOf<BeanDefinition<*>>()
    val subContexts = arrayListOf<Context>()
    var parentScope: KClass<*>? = null

    /*
     * Dependency declaration
     */

    /**
     * Create Root Context function
     */
    fun subContext(newScope: KClass<*>, init: Context.() -> Unit): Context {
        val newContext = Context(newScope, koinContext)
        newContext.parentScope = scope
        subContexts += newContext
        return newContext.apply(init)
    }

    infix fun dependsOn(clazz: KClass<*>) {
        parentScope = clazz
    }

    /**
     * Provide a bean definition
     * with a name
     */
    inline fun <reified T : Any> provide(name: String = "", noinline definition: () -> T): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(name, T::class, definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /*
        Dependency resolvers
     */

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(): T = null as T //koinContext.resolveByClass() // scope

    //TODO Classe Android / Autorelease (Act ou frag)?

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(name: String): T = null as T  //koinContext.resolveByName(name) // scope

    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T = koinContext.propertyResolver.getProperty(key)
}