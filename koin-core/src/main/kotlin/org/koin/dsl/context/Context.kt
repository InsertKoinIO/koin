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
     * Provide a bean definition - default provider definition
     * @param name
     * @param isSingleton
     */
    @Deprecated("Now use `bean` (for singletons) or `factory` (for factories)")
    inline fun <reified T : Any> provide(name: String = "", isSingleton: Boolean = true, noinline definition: () -> T): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(name, T::class, isSingleton = isSingleton, definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a bean definition - alias to provide
     * @param name
     */
    inline fun <reified T : Any> bean(name: String = "", noinline definition: () -> T): BeanDefinition<T> {
        return provide(name, true, definition)
    }

    /**
     * Provide a factory bean definition - factory provider
     * (recreate instance each time)
     *
     * @param name
     */
    inline fun <reified T : Any> factory(name: String = "", noinline definition: () -> T): BeanDefinition<T> {
        return provide(name, false, definition)
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
    inline fun <reified T> getProperty(key: String): T = koinContext.propertyResolver.getProperty(key)

    /**
     * Retrieve a property
     * @param key - property key
     * @param defaultValue - default value
     */
    inline fun <reified T> getProperty(key: String, defaultValue: T) = koinContext.propertyResolver.getProperty(key, defaultValue)

    // String display
    override fun toString(): String = "Context[$name]"
}