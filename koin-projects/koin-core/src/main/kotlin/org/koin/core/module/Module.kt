/*
 * Copyright 2017-2020 the original author or authors.
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

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.ScopeDSL

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
class Module(
    val createAtStart: Boolean,
    val override: Boolean
) {
    val rootScope: Qualifier = ScopeDefinition.ROOT_SCOPE_QUALIFIER
    internal var isLoaded: Boolean = false
    val scopes = arrayListOf<Qualifier>()
    val definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param qualifier
     */
    fun scope(qualifier: Qualifier, scopeSet: ScopeDSL.() -> Unit) {
        ScopeDSL(qualifier, definitions).apply(scopeSet)
        scopes.add(qualifier)
    }

    /**
     * Class Typed Scope
     */
    inline fun <reified T> scope(scopeSet: ScopeDSL.() -> Unit) {
        val qualifier = TypeQualifier(T::class)
        ScopeDSL(qualifier, definitions).apply(scopeSet)
        scopes.add(qualifier)
    }

    /**
     * Declare a Single definition
     * @param qualifier
     * @param createdAtStart
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> single(
        qualifier: Qualifier? = null,
        createdAtStart: Boolean = false,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val def = Definitions.createSingle(
            qualifier,
            definition,
            makeOptions(override, createdAtStart),
            scopeQualifier = rootScope,
        )
        definitions.add(def)
        return def
    }

    fun makeOptions(override: Boolean, createdAtStart: Boolean = false): Options =
        Options(this.createAtStart || createdAtStart, this.override || override)

    /**
     * Declare a Factory definition
     * @param qualifier
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val def = Definitions.createFactory(qualifier, definition, makeOptions(override), scopeQualifier = rootScope)
        definitions.add(def)
        return def
    }

    /**
     * Help write list of Modules
     */
    operator fun plus(module: Module) = listOf(this, module)

    /**
     * Help write list of Modules
     */
    operator fun plus(modules: List<Module>) = listOf(this) + modules
}

/**
 * Help write list of Modules
 */
operator fun List<Module>.plus(module: Module): List<Module> = this + listOf(module)