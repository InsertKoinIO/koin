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
import org.koin.core.error.NoScopeDefFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.core.scope.ScopeID

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
class ScopeRegistry(private val _koin: Koin) {

    //TODO Lock - ConcurrentHashMap
    private val _scopeDefinitions = HashMap<QualifierValue, ScopeDefinition>()
    val scopeDefinitions: Map<QualifierValue, ScopeDefinition>
        get() = _scopeDefinitions

    private val _scopes = HashMap<ScopeID, Scope>()
    val scopes: Map<ScopeID, Scope>
        get() = _scopes

    var _rootScopeDefinition: ScopeDefinition? = null
    var _rootScope: Scope? = null
    val rootScope: Scope
        get() = _rootScope ?: error("No root scoped initialized")

    fun size() = scopeDefinitions.values.map { it.size() }.sum()

    internal fun loadModules(modules: Iterable<Module>) {
        modules.forEach { module ->
            if (!module.isLoaded) {
                loadModule(module)
                module.isLoaded = true
            } else {
                _koin._logger.error("module '$module' already loaded!")
            }
        }
    }

    private fun loadModule(module: Module) {
        declareScope(module.rootScope)
        declareScopes(module.otherScopes)
    }

    private fun declareScopes(list: List<ScopeDefinition>) {
        list.forEach { scopeDefinition ->
            declareScope(scopeDefinition)
        }
    }

    private fun declareScope(scopeDefinition: ScopeDefinition) {
        declareDefinitions(scopeDefinition)
        declareInstances(scopeDefinition)
    }

    private fun declareInstances(scopeDefinition: ScopeDefinition) {
        _scopes.values.filter { it._scopeDefinition == scopeDefinition }.forEach { it.loadDefinitions(scopeDefinition) }
    }

    private fun declareDefinitions(definition: ScopeDefinition) {
        if (scopeDefinitions.contains(definition.qualifier.value)) {
            mergeDefinitions(definition)
        } else {
            _scopeDefinitions[definition.qualifier.value] = definition.copy()
        }
    }

    private fun mergeDefinitions(definition: ScopeDefinition) {
        val existing = scopeDefinitions[definition.qualifier.value]
                ?: error("Scope definition '$definition' not found in $_scopeDefinitions")
        definition.definitions.forEach {
            existing.save(it)
        }
    }

    internal fun createRootScopeDefinition() {
        val scopeDefinition = ScopeDefinition.rootDefinition()
        _scopeDefinitions[ScopeDefinition.ROOT_SCOPE_QUALIFIER.value] =
                scopeDefinition
        _rootScopeDefinition = scopeDefinition
    }

    internal fun createRootScope() {
        if (_rootScope == null) {
            _rootScope =
                    createScope(ScopeDefinition.ROOT_SCOPE_ID, ScopeDefinition.ROOT_SCOPE_QUALIFIER)
        }
    }

    fun getScopeOrNull(scopeId: ScopeID): Scope? {
        return scopes[scopeId]
    }

    fun createScope(scopeId: ScopeID, qualifier: Qualifier): Scope {
        if (scopes.contains(scopeId)) {
            throw ScopeAlreadyCreatedException("Scope with id '$scopeId' is already created")
        }

        val scopeDefinition = scopeDefinitions[qualifier.value]
                ?: _koin.parent?._scopeRegistry?.scopeDefinitions?.get(qualifier.value)

        return if (scopeDefinition != null) {
            val createdScope: Scope = createScope(scopeId, scopeDefinition)
            _scopes[scopeId] = createdScope
            createdScope
        } else {
            throw NoScopeDefFoundException("No Scope Definition found for qualifer '${qualifier.value}'")
        }
    }

    private fun createScope(scopeId: ScopeID, scopeDefinition: ScopeDefinition): Scope {
        val scope = Scope(scopeId, scopeDefinition, _koin)
        scope.create(_rootScope)
        return scope
    }

    //TODO Lock
    fun deleteScope(scopeId: ScopeID) {
        _scopes.remove(scopeId)
    }

    fun deleteScope(scope: Scope) {
        _scopes.remove(scope.id)
    }

    internal fun close() {
        _scopes.clear()
        _scopeDefinitions.clear()
        _rootScope?.close()
        _rootScope = null
    }

    fun unloadModules(modules: Iterable<Module>) {
        modules.forEach {
            unloadModules(it)
        }
    }

    fun unloadModules(module: Module) {
        val scopeDefinitions = module.otherScopes + module.rootScope
        scopeDefinitions.forEach {
            unloadDefinitions(it)
        }
        module.isLoaded = false
    }

    private fun unloadDefinitions(scopeDefinition: ScopeDefinition) {
        unloadInstances(scopeDefinition)
        _scopeDefinitions.values.firstOrNull { it == scopeDefinition }?.unloadDefinitions(scopeDefinition)
    }

    private fun unloadInstances(scopeDefinition: ScopeDefinition) {
        _scopes.values.filter { it._scopeDefinition == scopeDefinition }.forEach { it.dropInstances(scopeDefinition) }
    }
}