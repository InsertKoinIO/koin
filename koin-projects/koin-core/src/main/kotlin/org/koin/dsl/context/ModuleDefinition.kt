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
 *
 * @param path - module path
 * @param eager - module's definition are eager
 * @param override - module's definition can override
 * @param koinContext
 */
class ModuleDefinition(
    val path: String = "",
    val eager: Boolean = false,
    val override: Boolean = false,
    val koinContext: KoinContext
) {

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
    @Deprecated("use module { } function instead")
    fun context(path: String, init: ModuleDefinition.() -> Unit): ModuleDefinition = module(path, false, false, init)

    /**
     * Create a inner sub module in actual module
     * @param path - module path
     * @param eager - all definition are eager
     */
    fun module(
        path: String,
        eager: Boolean = false,
        override: Boolean = false,
        init: ModuleDefinition.() -> Unit
    ): ModuleDefinition {
        val newContext = ModuleDefinition(path, eager, override, koinContext)
        subModules += newContext
        return newContext.apply(init)
    }

    /**
     * Provide a singleton definition - default provider definition
     * @param name
     * @param eager - need to be created at start
     * @param override - allow override of the definition
     * @param isSingleton
     */
    inline fun <reified T : Any> provide(
        name: String = "",
        eager: Boolean = false,
        override: Boolean = false,
        isSingleton: Boolean = true,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition =
            BeanDefinition(
                name,
                T::class,
                isSingleton = isSingleton,
                isEager = eager,
                allowOverride = override,
                definition = definition
            )
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a singleton definition - alias to provide
     * Deprecated - @see single
     * @param name
     */
    inline fun <reified T : Any> bean(name: String = "", noinline definition: Definition<T>): BeanDefinition<T> =
        single(name, false, false, definition)

    /**
     * Provide a bean definition - alias to provide
     * @param name
     * @param eager - need to be created at start
     * @param override - allow definition override
     * @param definition
     */
    inline fun <reified T : Any> single(
        name: String = "",
        eager: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return provide(name, eager, override, true, definition)
    }

    /**
     * Provide a factory bean definition - factory provider
     * (recreate instance each time)
     *
     * @param name
     * @param eager - need to be created at start
     * @param override - allow definition override
     * @param definition
     */
    inline fun <reified T : Any> factory(
        name: String = "",
        eager: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return provide(name, eager, override, false, definition)
    }

    /**
     * Resolve a component
     * @param name : component name
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> get(
        name: String? = null,
        noinline parameters: ParameterDefinition = emptyParameterDefinition()
    ): T =
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