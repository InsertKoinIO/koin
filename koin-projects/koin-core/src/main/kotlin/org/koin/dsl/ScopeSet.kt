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

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeDefinition

/**
 * DSL Scope Definition
 */
data class ScopeSet(val qualifier: Qualifier) {

    val definitions: HashSet<BeanDefinition<*>> = hashSetOf()

    @Deprecated("Can't use Single in a scope. Use Scoped instead", level = DeprecationLevel.ERROR)
    inline fun <reified T> single(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        error("Scoped definition is deprecated and has been replaced with Single scope definitions")
    }

    inline fun <reified T> scoped(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = DefinitionFactory.createScoped(qualifier, this.qualifier, definition)
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
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = DefinitionFactory.createFactory(qualifier, this.qualifier, definition)
        declareDefinition(beanDefinition, Options(false, override))
        if (!definitions.contains(beanDefinition)) {
            definitions.add(beanDefinition)
        } else {
            throw DefinitionOverrideException("Can't add definition $beanDefinition for scope ${this.qualifier} as it already exists")
        }
        return beanDefinition
    }

    fun createDefinition(): ScopeDefinition {
        val scopeDefinition = ScopeDefinition(qualifier)
        scopeDefinition.definitions.addAll(definitions)
        return scopeDefinition
    }

    fun <T> declareDefinition(definition: BeanDefinition<T>, options: Options) {
        definition.updateOptions(options)
    }

    private fun BeanDefinition<*>.updateOptions(options: Options) {
        this.options.isCreatedAtStart = options.isCreatedAtStart
        this.options.override = options.override
    }

    override fun toString(): String {
        return "Scope['$qualifier']"
    }
}