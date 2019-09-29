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
package org.koin.dsl

import org.koin.core.definition.*
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.DefaultScope
import org.koin.core.scope.ObjectScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition

/**
 * DSL Scope Definition
 */
data class ScopeSet<S: Scope>(val factory: DefinitionFactory<S>, val qualifier: Qualifier, val validateParentScope: Boolean) {

    val childScopes = mutableListOf<ScopeSet<*>>()
    val definitions: HashSet<BeanDefinition<S, *>> = hashSetOf()

    @Deprecated("Can't use Single in a scope. Use Scoped instead", level = DeprecationLevel.ERROR)
    inline fun <reified T> single(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<S, T>
    ): BeanDefinition<S, T> {
        error("Scoped definition is deprecated and has been replaced with Single scope definitions")
    }

    inline fun <reified T> scoped(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<S, T>
    ): BeanDefinition<S, T> {
        val beanDefinition = factory.createScoped(qualifier, this.qualifier, definition)
        declareDefinition(beanDefinition, Options(false, override))
        if (!definitions.contains(beanDefinition)) {
            definitions.add(beanDefinition)
        } else {
            throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
        }
        return beanDefinition
    }

    inline fun <reified T> factory(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<S, T>
    ): BeanDefinition<S, T> {
        val beanDefinition = factory.createFactory(qualifier, this.qualifier, definition)
        declareDefinition(beanDefinition, Options(false, override))
        if (!definitions.contains(beanDefinition)) {
            definitions.add(beanDefinition)
        } else {
            throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
        }
        return beanDefinition
    }

    /**
     * Provides the ability to declare a child scope definition inside a ScopeSet.
     * @param scopeName
     * @param validateParentScope
     */
    @JvmOverloads
    inline fun <reified T> childObjectScope(
            scopeName: Qualifier = named<T>(),
            validateParentScope: Boolean = false,
            noinline scopeSet: (ScopeSet<ObjectScope<T>>.() -> Unit)? = null) {
        val scope = ScopeSet<ObjectScope<T>>(
                definitionFactory(),
                scopeName,
                validateParentScope
        )
        scopeSet?.let { scope.apply(it) }
        scope.declareScopedInstanceIfPossible()
        childScopes.add(scope)
    }

    inline fun <reified T> ScopeSet<ObjectScope<T>>.declareScopedInstanceIfPossible() {
        if (!this.definitions.any { it.primaryType == T::class }) {
            this.declareDefinition( scoped { instance }, Options())
        }
    }

    /**
     * Declare a child scope definition inside a ScopeSet.
     * @param scopeName
     * @param validateParentScope Validate the parent scope at scope instance creation
     */
    @JvmOverloads
    inline fun <reified T> typedChildScope(
            validateParentScope: Boolean = false,
            noinline scopeSet: ScopeSet<DefaultScope>.() -> Unit) {
        return childScope(TypeQualifier(T::class), validateParentScope, scopeSet)
    }

    /**
     * Declare a child scope definition inside a ScopeSet.
     * @param scopeName
     * @param validateParentScope Validate the parent scope at scope instance creation
     */
    @JvmOverloads
    fun childScope(
            scopeName: Qualifier,
            validateParentScope: Boolean = false,
            scopeSet: (ScopeSet<DefaultScope>.() -> Unit)? = null) {
        val scope = ScopeSet<DefaultScope>(
                definitionFactory(),
                scopeName,
                validateParentScope
        )
        scopeSet?.let { scope.apply(it) }
        childScopes.add(scope)
    }

    fun createDefinition(parentDefinition: ScopeDefinition?): ScopeDefinition {
        val scopeDefinition = ScopeDefinition(qualifier, validateParentScope, parentDefinition)
        scopeDefinition.definitions.addAll(definitions)
        return scopeDefinition
    }

    fun <T> declareDefinition(definition: BeanDefinition<S, T>, options: Options) {
        definition.updateOptions(options)
    }

    private fun BeanDefinition<*, *>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart
        this.options.override = options.override
    }

    override fun toString(): String {
        return "Scope['$qualifier']"
    }
}