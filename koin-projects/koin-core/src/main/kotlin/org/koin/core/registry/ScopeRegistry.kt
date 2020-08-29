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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.definition.BeanDefinition
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

    private var _rootScopeDefinition: ScopeDefinition? = null
    val rootScopeDefinition: ScopeDefinition
        get() = _rootScopeDefinition ?: error("No root scope definition")
    var _rootScope: Scope? = null
    val rootScope: Scope
        get() = _rootScope ?: error("No root scope")

    fun size() = _scopeDefinitions.values.map { it.size() }.sum()

    internal fun loadModules(modules: Iterable<Module>) {
        modules.forEach { module ->
            if (!module.isLoaded) {
                loadModule(module)
            } else {
                _koin._logger.error("module '$module' already loaded!")
            }
        }
    }

    private fun loadModule(module: Module) {
        declareDefinitions(module.definitions)
        module.isLoaded = true
    }

    private fun declareDefinitions(definitions: HashSet<BeanDefinition<*>>) {
        definitions.forEach { bean ->
            val scopeDef = _scopeDefinitions[bean.scopeQualifier.value] ?: ScopeDefinition(bean.scopeQualifier)
            scopeDef.save(bean)
            _scopes.values.filter { it._scopeDefinition == scopeDef }.forEach { it.loadDefinition(bean) }
        }
    }

    internal fun createRootScopeDefinition() {
        if (_rootScopeDefinition == null) {
            val scopeDefinition = ScopeDefinition.rootDefinition()
            _scopeDefinitions[ScopeDefinition.ROOT_SCOPE_QUALIFIER.value] =
                scopeDefinition
            _rootScopeDefinition = scopeDefinition
        } else error("Try to recreate Root scope definition")
    }

    internal fun createRootScope() {
        if (_rootScope == null) {
            _rootScope =
                createScope(ScopeDefinition.ROOT_SCOPE_ID, ScopeDefinition.ROOT_SCOPE_QUALIFIER, null)
        } else error("Try to recreate Root scope")
    }

    fun getScopeOrNull(scopeId: ScopeID): Scope? {
        return scopes[scopeId]
    }

    fun createScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null): Scope {
        if (scopes.contains(scopeId)) {
            throw ScopeAlreadyCreatedException("Scope with id '$scopeId' is already created")
        }

        val scopeDefinition = _scopeDefinitions[qualifier.value]
        return if (scopeDefinition != null) {
            val createdScope: Scope = createScope(scopeId, scopeDefinition, source)
            _scopes[scopeId] = createdScope
            createdScope
        } else {
            throw NoScopeDefFoundException("No Scope Definition found for qualifer '${qualifier.value}'")
        }
    }

    private fun createScope(scopeId: ScopeID, scopeDefinition: ScopeDefinition, source: Any?): Scope {
        val scope = Scope(scopeId, scopeDefinition, _koin).apply { _source = source }
        val links = _rootScope?.let { listOf(it) } ?: emptyList()
        scope.create(links)
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
        clearScopes()
        _scopes.clear()
        _scopeDefinitions.clear()
        _rootScopeDefinition = null
        _rootScope = null
    }

    private fun clearScopes() {
        _scopes.values.forEach { scope -> scope.clear() }
    }

    fun unloadModules(modules: Iterable<Module>) {
        modules.forEach {
            unloadModules(it)
        }
    }

    fun unloadModules(module: Module) {
        val scopeDefinitions = module.scopes + rootScopeDefinition.qualifier
        scopeDefinitions.forEach {
            unloadDefinitions(it)
        }
        module.isLoaded = false
    }

    private fun unloadDefinitions(scopeDefinition: Qualifier) {
        unloadInstances(scopeDefinition)
        _scopeDefinitions.values.firstOrNull { it.qualifier.value == scopeDefinition.value }?.let {
            it.unloadDefinitions(it)
        }
    }

    private fun unloadInstances(scopeDefinition: Qualifier) {
        _scopes.values.filter { it._scopeDefinition.qualifier.value == scopeDefinition.value }.forEach {
            it.dropInstances(it._scopeDefinition)
        }
    }
}