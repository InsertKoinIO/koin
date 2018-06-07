package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.parameter.Parameters
import org.koin.core.scope.Scope

/**
 * Koin Context
 * Define dependencies & properties for actual context
 *
 * @author - Arnaud GIULIANI
 */
class Context(val name: String = Scope.ROOT, val koinContext: KoinContext, val override: Boolean) {

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
     * @param override
     */
    fun context(name: String, override: Boolean? = null, init: Context.() -> Unit): Context {
        val newContext = Context(name, koinContext, override ?: this@Context.override)
        subContexts += newContext
        return newContext.apply(init)
    }

    /**
     * Provide a bean definition - default provider definition
     * @param name
     * @param isSingleton
     */
    @Deprecated("use `bean` (for singleton instances) or `factory` (for factory instances)")
    inline fun <reified T : Any> provide(
        name: String = "",
        isSingleton: Boolean = true,
        override: Boolean? = null,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(
            name, T::class,
            isSingleton = isSingleton,
            override = override ?: this@Context.override,
            definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a bean definition - alias to provide
     * @param name
     */
    inline fun <reified T : Any> bean(name: String = "", override: Boolean? = null, noinline definition: Definition<T>): BeanDefinition<T> {
        return provide(name, true, override, definition)
    }

    /**
     * Provide a factory bean definition - factory provider
     * (recreate instance each time)
     *
     * @param name
     */
    inline fun <reified T : Any> factory(name: String = "", override: Boolean? = null, noinline definition: Definition<T>): BeanDefinition<T> {
        return provide(name, false, override, definition)
    }

    /**
     * Resolve a component
     * @param name : component name
     */
    inline fun <reified T : Any> get(name: String? = null): T = if (name != null) koinContext.resolveByName(
        name,
        { emptyMap() }) else koinContext.resolveByClass({ emptyMap() })

    /**
     * Resolve a component
     * @param name : component name
     * @param parameters - dynamic parameters
     */
    inline fun <reified T : Any> get(name: String? = null, noinline parameters: Parameters): T =
        if (name != null) koinContext.resolveByName(name, parameters) else koinContext.resolveByClass(parameters)

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
    inline fun <reified T> getProperty(key: String, defaultValue: T) =
        koinContext.propertyResolver.getProperty(key, defaultValue)

    // String display
    override fun toString(): String = "Context[$name]"
}
