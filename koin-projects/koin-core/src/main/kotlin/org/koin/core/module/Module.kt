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

import org.koin.core.definition.*
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.DefaultScope
import org.koin.core.scope.ObjectScope
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
class Module(
        internal val isCreatedAtStart: Boolean,
        internal val override: Boolean
) {
    internal val definitions = arrayListOf<BeanDefinition<*, *>>()
    internal val scopes = arrayListOf<ScopeSet<*>>()

    /**
     * Declare a definition in current Module
     */
    fun <S: Scope, T> declareDefinition(definition: BeanDefinition<S, T>, options: Options) {
        definition.updateOptions(options)
        definitions.add(definition)
    }

    /**
     * Declare a definition in current Module
     */
    fun declareScope(scope: ScopeSet<*>) {
        scopes.add(scope)
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
            noinline definition: Definition<RootScope, T>
    ): BeanDefinition<RootScope, T> {
        val beanDefinition = definitionFactory<RootScope>().createScoped(qualifier, definition = definition)
        declareDefinition(beanDefinition, Options(createdAtStart, override))
        return beanDefinition
    }

    private fun BeanDefinition<*, *>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart || isCreatedAtStart
        this.options.override = options.override || override
    }

    /**
     * Declare a group a scoped definition. The qualifier is determined by the associated type.
     * @param scopeName
     */
    inline fun <reified T> typedScope(scopeSet: ScopeSet<DefaultScope>.() -> Unit) {
        return scope(TypeQualifier(T::class), scopeSet)
    }

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param scopeName
     */
    inline fun scope(
            scopeName: Qualifier,
            scopeSet: ScopeSet<DefaultScope>.() -> Unit) {
        val scope = ScopeSet<DefaultScope>(
                definitionFactory(),
                scopeName,
                validateParentScope = false
        ).apply(scopeSet)
        declareScope(scope)
    }

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param scopeName
     */
    inline fun <reified T> objectScope(
            scopeName: Qualifier = named<T>(),
            noinline scopeSet: ScopeSet<ObjectScope<T>>.() -> Unit) {
        val scope = ScopeSet<ObjectScope<T>>(
                definitionFactory(),
                scopeName,
                validateParentScope = false
        ).apply(scopeSet)
        scope.declareScopedInstanceIfPossible()
        declareScope(scope)
    }

    inline fun <reified T> ScopeSet<ObjectScope<T>>.declareScopedInstanceIfPossible() {
        if (!definitions.any { it.primaryType == T::class }) {
            this.declareDefinition( scoped { instance }, Options())
        }
    }

    /**
     * Declare a Factory definition
     * @param qualifier
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> factory(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<RootScope, T>
    ): BeanDefinition<RootScope, T> {
        val beanDefinition = definitionFactory<RootScope>()
                .createFactory(qualifier, definition = definition)
        declareDefinition(beanDefinition, Options(override = override))
        return beanDefinition
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