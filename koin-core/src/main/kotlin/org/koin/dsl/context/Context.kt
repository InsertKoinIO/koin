package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope

/**
 * Koin Context
 * Define dependencies & properties for actual context
 *
 * @author - Arnaud GIULIANI
 */
class Context(val name: String = Scope.ROOT, val koinContext: KoinContext) {

    /**
     * bean definitions
     */
    val definitions = arrayListOf<BeanDefinition<*>>()

    /**
     * sub contexts
     */
    val subContexts = arrayListOf<Context>()

    /**
     * Create a sub context in actual context
     * @param name
     */
    fun context(name: String, init: Context.() -> Unit): Context {
        val newContext = Context(name, koinContext)
        subContexts += newContext
        return newContext.apply(init)
    }

    /**
     * Provide a bean definition
     * @param name
     * @param isSingleton
     */
    inline fun <reified T : Any> provide(name: String = "", isSingleton: Boolean = true, noinline definition: () -> T): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(name, T::class, isSingleton, definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a bean definition - as factory (recreate instance each time)
     */
    inline fun <reified T : Any> provideFactory(name: String = "", noinline definition: () -> T): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(name, T::class, false, definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Set a property
     * @param pair / Key / Value
     */
    fun property(pair: Pair<String, Any>) {
        koinContext.setProperty(pair.first, pair.second)
    }

    /**
     * Set properties
     * @param pairs : list of Pair / Key Value
     */
    fun properties(pairs: List<Pair<String, Any>>) {
        pairs.forEach { property(it) }
    }

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(): T = koinContext.resolveByClass()

    /**
     * Resolve a component
     * @param name : component name
     */
    inline fun <reified T : Any> get(name: String): T = koinContext.resolveByName(name)

    /**
     * Retrieve a property
     * @param key - property key
     */
    inline fun <reified T> getProperty(key: String): T = koinContext.getProperty(key)

    /**
     * Retrieve a property
     * @param key - property key
     * @param defaultValue - default value T
     */
    inline fun <reified T> getProperty(key: String, defaultValue: T) = koinContext.getProperty(key, defaultValue)

    // String display
    override fun toString(): String = "Context[$name]"
}