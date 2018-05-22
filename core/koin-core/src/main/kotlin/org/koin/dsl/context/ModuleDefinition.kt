package org.koin.dsl.context

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.definition.Definition

/**
 * Koin Module Definition
 * Gather dependencies & properties definitions
 *
 * @author - Arnaud GIULIANI
 */
class ModuleDefinition(val path: String = "", val koinContext: KoinContext) {

    /**
     * bean definitions
     */
    val definitions = arrayListOf<BeanDefinition<*>>()

    /**
     * sub modules
     */
    val subModules = arrayListOf<ModuleDefinition>()

    /**
     * Create a inner sub module in actual module
     * @param path
     */
    @Deprecated("use module { }")
    fun context(path: String, init: ModuleDefinition.() -> Unit): ModuleDefinition = module(path, init)

    /**
     * Create a inner sub module in actual module
     * @param path
     */
    fun module(path: String, init: ModuleDefinition.() -> Unit): ModuleDefinition {
        val newContext = ModuleDefinition(path, koinContext)
        subModules += newContext
        return newContext.apply(init)
    }

    /**
     * Provide a singleton definition - default provider definition
     * @param name
     * @param isSingleton
     */
    inline fun <reified T : Any> provide(
        name: String = "",
        isSingleton: Boolean = true,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition =
            BeanDefinition(name, T::class, isSingleton = isSingleton, definition = definition)
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a singleton definition - alias to provide
     * Deprecated - @see single
     * @param name
     */
    inline fun <reified T : Any> bean(name: String = "", noinline definition: Definition<T>): BeanDefinition<T> =
        single(name, definition)

    /**
     * Provide a bean definition - alias to provide
     * @param name
     */
    inline fun <reified T : Any> single(name: String = "", noinline definition: Definition<T>): BeanDefinition<T> {
        return provide(name, true, definition)
    }

    /**
     * Provide a factory bean definition - factory provider
     * (recreate instance each time)
     *
     * @param name
     */
    inline fun <reified T : Any> factory(name: String = "", noinline definition: Definition<T>): BeanDefinition<T> {
        return provide(name, false, definition)
    }

    /**
     * Resolve a component
     * @param name : component name
     */
    inline fun <reified T : Any> get(name: String? = null): T = if (name != null) koinContext.resolveByName(
        name, parameters = emptyParameterDefinition()
    ) else koinContext.resolveByClass(parameters = emptyParameterDefinition())

    /**
     * Resolve a component
     * @param name : component name
     * @param parameters - dynamic parameters
     */
    inline fun <reified T : Any> get(name: String? = null, noinline parameters: ParameterDefinition): T =
        if (name != null) koinContext.resolveByName(name, parameters = parameters) else koinContext.resolveByClass(
            parameters = parameters
        )

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
    override fun toString(): String = "ModuleDefinition[$path]"
}