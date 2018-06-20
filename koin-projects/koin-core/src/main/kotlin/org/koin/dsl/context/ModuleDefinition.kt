/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author - Laurent Baresse
 *
 * @param path - module path
 * @param createOnStart - module's definition are created on Koin's start
 * @param override - module's definition can override
 * @param koinContext
 */
class ModuleDefinition(
    val path: String = "",
    val createOnStart: Boolean = false,
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
     * @param createOnStart - all definition are created On Start
     */
    fun module(
        path: String,
        createOnStart: Boolean = false,
        override: Boolean = false,
        init: ModuleDefinition.() -> Unit
    ): ModuleDefinition {
        val newContext = ModuleDefinition(path, createOnStart, override, koinContext)
        subModules += newContext
        return newContext.apply(init)
    }

    /**
     * Provide a singleton definition - default provider definition
     * @param name
     * @param createOnStart - need to be created at start
     * @param override - allow override of the definition
     * @param isSingleton
     */
    inline fun <reified T : Any> provide(
        name: String = "",
        createOnStart: Boolean = false,
        override: Boolean = false,
        isSingleton: Boolean = true,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition =
            BeanDefinition(
                name,
                T::class,
                isSingleton = isSingleton,
                isEager = createOnStart,
                allowOverride = override,
                definition = definition
            )
        definitions += beanDefinition
        return beanDefinition
    }

    /**
     * Provide a singleton definition - alias to provide
     *
     * Deprecated - @see single
     * @param name
     */
    @Deprecated("use single { } function instead")
    inline fun <reified T : Any> bean(name: String = "", noinline definition: Definition<T>): BeanDefinition<T> =
        single(name, false, false, definition)

    /**
     * Provide a bean definition - alias to provide
     * @param name
     * @param createOnStart - need to be created at start
     * @param override - allow definition override
     * @param definition
     */
    inline fun <reified T : Any> single(
        name: String = "",
        createOnStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return provide(name, createOnStart, override, true, definition)
    }

    /**
     * Provide a factory bean definition - factory provider
     * (recreate instance each time)
     *
     * @param name
     * @param createOnStart - need to be created at start
     * @param override - allow definition override
     * @param definition
     */
    inline fun <reified T : Any> factory(
        name: String = "",
        createOnStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return provide(name, createOnStart, override, false, definition)
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