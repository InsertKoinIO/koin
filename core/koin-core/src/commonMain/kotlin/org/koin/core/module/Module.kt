/*
 * Copyright 2017-2021 the original author or authors.
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
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.registry.ScopeRegistry.Companion.rootScopeQualifier
import org.koin.dsl.ScopeDSL

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
class Module {
    var eagerInstances = hashSetOf<SingleInstanceFactory<*>>()
        internal set

    @PublishedApi
    internal val mappings = hashMapOf<IndexKey, InstanceFactory<*>>()

    val isLoaded: Boolean
        get() = mappings.size > 0

    @PublishedApi
    internal val scopes = hashSetOf<Qualifier>()

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param qualifier
     */
    fun scope(qualifier: Qualifier, scopeSet: ScopeDSL.() -> Unit) {
        ScopeDSL(qualifier, this).apply(scopeSet)
        scopes.add(qualifier)
    }

    /**
     * Class Typed Scope
     */
    inline fun <reified T> scope(scopeSet: ScopeDSL.() -> Unit) {
        val qualifier = TypeQualifier(T::class)
        ScopeDSL(qualifier, this).apply(scopeSet)
        scopes.add(qualifier)
    }

    /**
     * Declare a Single definition
     * @param qualifier
     * @param createdAtStart
     * @param definition - definition function
     */
    inline fun <reified T> single(
        qualifier: Qualifier? = null,
        createdAtStart: Boolean = false,
        noinline definition: Definition<T>
    ): Pair<Module, InstanceFactory<T>> {
        val def = createDefinition(Kind.Singleton, qualifier, definition, scopeQualifier = rootScopeQualifier)
        val mapping = indexKey(def.primaryType, qualifier, rootScopeQualifier)
        val instanceFactory = SingleInstanceFactory(def)
        saveMapping(mapping, instanceFactory)
        if (createdAtStart) {
            eagerInstances.add(instanceFactory)
        }
        return Pair(this, instanceFactory)
    }

    @PublishedApi
    internal fun saveMapping(mapping: IndexKey, factory: InstanceFactory<*>) {
        if (mappings.contains(mapping)) {
            overrideError(factory, mapping)
        }
        mappings[mapping] = factory
    }

    /**
     * Declare a Factory definition
     * @param qualifier
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>
    ): Pair<Module, InstanceFactory<T>> {
        return factory(qualifier, definition, rootScopeQualifier)
    }

    @PublishedApi
    internal inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeQualifier: Qualifier
    ): Pair<Module, InstanceFactory<T>> {
        val def = createDefinition(Kind.Factory, qualifier, definition, scopeQualifier = scopeQualifier)
        val mapping = indexKey(def.primaryType, qualifier, scopeQualifier)
        val instanceFactory = FactoryInstanceFactory(def)
        saveMapping(mapping, instanceFactory)
        return Pair(this, instanceFactory)
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

@PublishedApi
internal fun overrideError(
    factory: InstanceFactory<*>,
    mapping: IndexKey
) {
    throw DefinitionOverrideException("Already existing definition for ${factory.beanDefinition} at $mapping")
}

//fun HashSet<BeanDefinition<*>>.addDefinition(bean : BeanDefinition<*>){
//    val added = add(bean)
//    if (!added && !bean.options.override){
//        throw DefinitionOverrideException(
//            "Definition '$bean' try to override existing definition. Please use override option to fix it")
//    } else if(!added && bean.options.override) {
//        remove(bean)
//        add(bean)
//    }
//}

///**
// * Help write list of Modules
// */
//operator fun List<Module>.plus(module: Module): List<Module> = this + listOf(module)