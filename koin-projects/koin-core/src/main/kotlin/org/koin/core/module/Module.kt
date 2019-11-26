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
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.Options
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.ScopeDSL

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
class Module(
    val createdAtStart: Boolean,
    val override: Boolean
) {
    val rootScope: ScopeDefinition = ScopeDefinition.rootDefinition()
    var isLoaded: Boolean = false
        internal set
    internal val otherScopes = arrayListOf<ScopeDefinition>()

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param qualifier
     */
    fun scope(qualifier: Qualifier, scopeSet: ScopeDSL.() -> Unit) {
        val scopeDefinition = ScopeDefinition(qualifier)
        ScopeDSL(scopeDefinition).apply(scopeSet)
        otherScopes.add(scopeDefinition)
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
        val beanDefinition = BeanDefinition(
            rootScope,
            T::class,
            qualifier,
            { koin: Koin, beanDef: BeanDefinition<*> -> SingleInstanceFactory(koin, beanDef) },
            definition,
            Kind.Single,
            options = makeOptions(createdAtStart, override)
        )
        rootScope.save(beanDefinition)
        return beanDefinition
    }

    fun makeOptions(override: Boolean, createdAtStart: Boolean = false): Options =
        Options(this.createdAtStart || createdAtStart, this.override || override)

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
        val beanDefinition = BeanDefinition(
            rootScope,
            T::class,
            qualifier,
            { koin: Koin, beanDef: BeanDefinition<*> -> FactoryInstanceFactory(koin, beanDef) },
            definition,
            Kind.Factory,
            options = makeOptions(override)
        )
        rootScope.save(beanDefinition)
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