package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val name: String = Scope.ROOT, val koinContext: KoinContext) {

    val definitions = arrayListOf<BeanDefinition<*>>()
    val subContexts = arrayListOf<Context>()

    /*
     * Dependency declaration
     */

    /**
     * Create Root Context function
     */
    fun context(name: String, init: Context.() -> Unit): Context {
        val newContext = Context(name, koinContext)
        subContexts += newContext
        return newContext.apply(init)
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
    inline fun <reified T : Any> get(): T = null as T //koinContext.resolveByClass() // getScope

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(name: String): T = null as T  //koinContext.resolveByName(name) // getScope

    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T = koinContext.propertyResolver.getProperty(key)

    override fun toString(): String = "Context[$name]"
}