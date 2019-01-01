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
package org.koin.core.module

import org.koin.core.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.bean.Options
import org.koin.core.error.MissingPropertyException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
class Module(internal val isCreatedAtStart: Boolean, internal val override: Boolean) {
    internal val definitions = arrayListOf<BeanDefinition<*>>()
    lateinit var koin: Koin

    /**
     * Declare a definition in current Module
     */
    fun <T> declareDefinition(definition: BeanDefinition<T>, options: Options) {
        definition.updateOptions(options)
        definitions.add(definition)
    }

    /**
     * Declare a Single definition
     * @param name
     * @param createdAtStart
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> single(
        name: String? = null,
        createdAtStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createSingle(name, definition)
        declareDefinition(beanDefinition, Options(createdAtStart, override))
        return beanDefinition
    }

    private fun BeanDefinition<*>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart || isCreatedAtStart
        this.options.override = options.override || override
    }

//    /**
//     * Declare a group a scoped definition
//     * @param scopeName
//     */
//    fun withScopeInstance(scopeName: String, scopeGroupDefinition: ScopeInstanceGroupDefinition) {
//        return ScopeInstanceGroup(scopeName, this).let(scopeGroupDefinition)
//    }

    /**
     * Declare a ScopeInstance definition
     * @param scopeKey
     * @param name
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> scoped(
        scopeKey: String? = null,
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createScope(name, scopeKey, definition)
        declareDefinition(beanDefinition, Options(override = override))
        return beanDefinition
    }

    /**
     * Declare a Factory definition
     * @param name
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> factory(
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createFactory(name, definition)
        declareDefinition(beanDefinition, Options(override = override))
        return beanDefinition
    }

    /**
     * Resolve an instance from Koin
     * @param name
     * @param scope
     * @param parameters
     */
    inline fun <reified T> get(
        name: String? = null,
        scope: ScopeInstance? = null,
        noinline parameters: ParametersDefinition? = null
    ): T {
        return koin.get(name, scope, parameters)
    }

//    /**
//     * Resolve an instance from Koin
//     * @param scopeName
//     */
//    fun getScopeInstance(
//        scopeName: String
//    ): ScopeInstance {
//        return koin.getScopeInstance(scopeName)
//    }

    /**
     * Get a property from Koin
     * @param key
     */
    fun <T> getProperty(key: String): T {
        return koin.getProperty(key) ?: throw MissingPropertyException("Property '$key' is missing")
    }

    /**
     * Help write list of Modules
     */
    operator fun plus(module: Module) = listOf(this, module)
}

/**
 * Help write list of Modules
 */
operator fun List<Module>.plus(module: Module): List<Module> = this + listOf(module)