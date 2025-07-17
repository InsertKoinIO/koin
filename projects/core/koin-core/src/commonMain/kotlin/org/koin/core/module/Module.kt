/*
 * Copyright 2017-Present the original author or authors.
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

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.*
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.registry.ScopeRegistry.Companion.rootScopeQualifier
import org.koin.dsl.ScopeDSL
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId

/**
 * Koin Module
 * Gather/help compose Koin definitions
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@KoinDslMarker
class Module(

    @PublishedApi
    internal val _createdAtStart: Boolean = false,
) {
    val id = KoinPlatformTools.generateId()

    var eagerInstances = LinkedHashSet<SingleInstanceFactory<*>>()
        internal set

    @KoinInternalApi
    val mappings = LinkedHashMap<IndexKey, InstanceFactory<*>>()

    val isLoaded: Boolean
        get() = mappings.isNotEmpty()


    @KoinInternalApi
    val scopes = LinkedHashSet<Qualifier>()

    @KoinInternalApi
    val includedModules = mutableListOf<Module>()

    /**
     * A collection of [Module] from which the current [Module] is compose.
     * Duplicated modules are ignored.
     */
    fun includes(vararg module: Module) {
        includedModules.addAll(module)
    }

    /**
     * A collection of [Module] from which the current [Module] is compose.
     * Duplicated modules are ignored.
     */
    fun includes(module: Collection<Module>) {
        includedModules.addAll(module)
    }

    /**
     * Declare a group a scoped definition with a given scope qualifier
     * @param qualifier
     */
    @KoinDslMarker
    fun scope(qualifier: Qualifier, scopeSet: ScopeDSL.() -> Unit) {
        ScopeDSL(qualifier, this).apply(scopeSet)
        scopes.add(qualifier)
    }

    /**
     * Class Typed Scope
     */
    @KoinDslMarker
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
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        val factory = _singleInstanceFactory(qualifier, definition)
        indexPrimaryType(factory)
        if (createdAtStart || this._createdAtStart) {
            prepareForCreationAtStart(factory)
        }
        return KoinDefinition(this, factory)
    }

    @KoinInternalApi
    fun indexPrimaryType(instanceFactory: InstanceFactory<*>) {
        val def = instanceFactory.beanDefinition
        val mapping = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        saveMapping(mapping, instanceFactory)
    }

    @KoinInternalApi
    fun indexSecondaryTypes(instanceFactory: InstanceFactory<*>) {
        val def = instanceFactory.beanDefinition
        def.secondaryTypes.forEach { clazz ->
            val mapping = indexKey(clazz, def.qualifier, def.scopeQualifier)
            saveMapping(mapping, instanceFactory)
        }
    }

    @KoinInternalApi
    fun prepareForCreationAtStart(instanceFactory: SingleInstanceFactory<*>) {
        eagerInstances.add(instanceFactory)
    }

    @PublishedApi
    internal fun saveMapping(mapping: IndexKey, factory: InstanceFactory<*>) {
        mappings[mapping] = factory
    }

    /**
     * Declare a Factory definition
     * @param qualifier
     * @param definition - definition function
     */
    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        return factory(qualifier, definition, rootScopeQualifier)
    }

    @PublishedApi
    internal inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeQualifier: Qualifier,
    ): KoinDefinition<T> {
        val factory = _factoryInstanceFactory(qualifier, definition, scopeQualifier)
        indexPrimaryType(factory)
        return KoinDefinition(this, factory)
    }

    /**
     * Help write list of Modules
     */
    operator fun plus(module: Module) = listOf(this, module)

    /**
     * Help write list of Modules
     */
    operator fun plus(modules: List<Module>) = listOf(this) + modules

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Module) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@PublishedApi
internal fun overrideError(
    factory: InstanceFactory<*>,
    mapping: IndexKey,
) {
    throw DefinitionOverrideException("Already existing definition for ${factory.beanDefinition} at $mapping")
}

@KoinInternalApi
inline fun <reified T> _singleInstanceFactory(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
    scopeQualifier: Qualifier = rootScopeQualifier,
): SingleInstanceFactory<T> {
    val def = _createDefinition(Kind.Singleton, qualifier, definition, scopeQualifier = scopeQualifier)
    return SingleInstanceFactory(def)
}

@KoinInternalApi
inline fun <reified T> _factoryInstanceFactory(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
    scopeQualifier: Qualifier = rootScopeQualifier,
): FactoryInstanceFactory<T> {
    val def = _createDefinition(Kind.Factory, qualifier, definition, scopeQualifier = scopeQualifier)
    return FactoryInstanceFactory(def)
}

@KoinInternalApi
inline fun <reified T> _scopedInstanceFactory(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
    scopeQualifier: Qualifier,
): ScopedInstanceFactory<T> {
    val def = _createDefinition(Kind.Scoped, qualifier, definition, scopeQualifier = scopeQualifier)
    return ScopedInstanceFactory(def)
}

/**
 * Help write list of Modules
 */
operator fun List<Module>.plus(module: Module): List<Module> = this + listOf(module)

/**
 * Run through the module list to flatten all modules & submodules
 */
@KoinInternalApi
fun flatten(modules: List<Module>): Set<Module> {
    // This is actually a DFS traversal of the module graph,
    // but we're using a stack instead of recursion to avoid stack overflows and performance overhead.

    val flatten = linkedSetOf<Module>()
    val stack = ArrayDeque(modules.asReversed())

    while (stack.isNotEmpty()) {
        val current = stack.removeLast()

        // If the module is already in the set, that means we've already visited it, so we can skip it.
        if (!flatten.add(current)) {
            continue
        }

        // Add all the included modules to the stack if they haven't been visited yet.
        for (module in current.includedModules.asReversed()) {
            if (module !in flatten) {
                stack += module
            }
        }
    }

    return flatten
}
