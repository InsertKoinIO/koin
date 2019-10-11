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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.NestedScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.core.scope.ScopeID
import org.koin.dsl.ScopeSet
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
class ScopeRegistry {

    internal val definitions = ConcurrentHashMap<String, ScopeDefinition>()
    private val instances = ConcurrentHashMap<String, Scope>()

    /**
     * return all ScopeSet
     */
    fun getScopeSets() = definitions.values

    internal fun loadScopes(modules: Iterable<Module>) {
        modules.forEach {
            declareScopes(it)
        }
    }

    internal fun unloadScopedDefinitions(modules: Iterable<Module>) {
        modules.forEach {
            unloadScopes(it)
        }
    }

    private fun unloadScopes(module: Module) {
        module.scopes.forEach {
            unloadScope(it)
        }
    }

    private fun unloadScope(scopeSet: ScopeSet<*>) {
        scopeSet.nestedScopes.forEach { scope -> unloadScope(scope) }
        unloadDefinition(scopeSet)
    }

    private fun declareScope(scopeSet: ScopeSet<*>, parentDefinition: ScopeDefinition?) {
        val definition = saveDefinition(scopeSet, parentDefinition)
        scopeSet.nestedScopes.forEach {
            declareScope(it, definition)
        }
    }

    fun loadDefaultScopes(koin: Koin) {
        saveInstance(koin.rootScope)
    }

    private fun declareScopes(module: Module) {
        val rootScope = getScopeInstance("-Root-")
        module.scopes.forEach {
            declareScope(it, rootScope.scopeDefinition)
        }
    }

    private fun unloadDefinition(scopeSet: ScopeSet<*>) {
        val key = scopeSet.qualifier.toString()
        definitions[key]?.let { scopeDefinition ->
            if (KoinApplication.logger.isAt(Level.DEBUG)) {
                KoinApplication.logger.info("unbind scoped definitions: ${scopeSet.definitions} from '${scopeSet.qualifier}'")
            }
            closeRelatedScopes(scopeDefinition)
            scopeDefinition.definitions.removeAll(scopeSet.definitions)
        }
    }

    private fun closeRelatedScopes(originalSet: ScopeDefinition) {
        val it = instances.iterator()
        while (it.hasNext()) {
            val (_, scope) = it.next()
            if (scope.scopeDefinition == originalSet) {
                scope.close()
                it.remove()
            }
        }
    }

    private fun saveDefinition(scopeSet: ScopeSet<*>, parentDefinition: ScopeDefinition?): ScopeDefinition {
        val foundScopeSet: ScopeDefinition? = definitions[scopeSet.qualifier.toString()]
        return if (foundScopeSet == null) {
            val definition = scopeSet.createDefinition(parentDefinition)
            definitions[scopeSet.qualifier.toString()] = definition
            definition
        } else {
            foundScopeSet.definitions.addAll(scopeSet.definitions)
            foundScopeSet
        }
    }

    fun getScopeDefinition(scopeName: String): ScopeDefinition? = definitions[scopeName]

    /**
     * Create a scope instance for given scope
     * @param id - scope instance id
     * @param scopeName - scope qualifier
     */
    fun createScopeInstance(koin: Koin, id: ScopeID, scopeName: Qualifier, parentScopeID: ScopeID): NestedScope {
        val definition: ScopeDefinition = definitions[scopeName.toString()]
                ?: throw NoScopeDefinitionFoundException("No scope definition found for scopeName '$scopeName'")
        val parentScope = getScopeInstanceOrNull(parentScopeID)
                ?: throw ScopeNotCreatedException("Couldn't retrieve parent scope with id: $parentScopeID for scope with id: $id and qualifier: $scopeName")
        val instance = NestedScope(id, _koin = koin, parentScope = parentScope)
        instance.scopeDefinition = definition
        instance.declareDefinitionsFromScopeSet()
        registerScopeInstance(instance)
        return instance
    }

    private fun registerScopeInstance(instance: Scope) {
        if (instances[instance.id] != null) {
            throw ScopeAlreadyCreatedException("A scope with id '${instance.id}' already exists. Reuse or close it.")
        }
        saveInstance(instance)
    }

    fun getScopeInstance(id: ScopeID): Scope {
        return instances[id]
                ?: throw ScopeNotCreatedException("ScopeInstance with id '$id' not found. Create a scope instance with id '$id'")
    }

    private fun saveInstance(instance: Scope) {
        instances[instance.id] = instance
    }

    fun getScopeInstanceOrNull(id: ScopeID): Scope? {
        return instances[id]
    }

    fun deleteScopeInstance(id: ScopeID) {
        val scope = getScopeInstanceOrNull(id)
        instances.remove(id)
        val scopes = instances.values.toList()
        scopes.mapNotNull { it as? NestedScope }
                .filter { it.parentScope === scope }
                .forEach { it.close() }
    }

    fun close() {
        instances.values.forEach { it.close() }
        definitions.clear()
        instances.clear()
    }
}