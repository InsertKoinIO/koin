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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternal
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
@OptIn(KoinInternal::class)
class ScopeRegistry(private val _koin: Koin) {

    private val _scopeDefinitions = HashMap<QualifierValue, ScopeDefinition>()
    val scopeDefinitions: Map<QualifierValue, ScopeDefinition>
        get() = _scopeDefinitions

    private val _scopes = HashMap<ScopeID, Scope>()

    private var _rootScopeDefinition: ScopeDefinition? = null

    private var _rootScope: Scope? = null
    val rootScope: Scope
        get() = _rootScope ?: error("No root scope")

    fun size() = _scopeDefinitions.values.map { it.size() }.sum()

    internal fun loadModules(modules: Iterable<Module>) {
        modules.forEach { module ->
            if (!module.isLoaded) {
                loadModule(module)
            } else {
                _koin.logger.error("module '$module' already loaded!")
            }
        }
    }

    private fun loadModule(module: Module) {
        declareScopeDefinitions(module.scopes)
        declareBeanDefinitions(module.definitions)
        module.isLoaded = true
    }

    private fun declareScopeDefinitions(scopes: List<Qualifier>) {
        scopes.forEach { scopeQualifier ->
            createScopeDefinition(scopeQualifier)
        }
    }

    private fun declareBeanDefinitions(definitions: HashSet<BeanDefinition<*>>) {
        definitions.forEach { bean ->
            declareDefinition(bean)
        }
    }

    fun declareDefinition(bean: BeanDefinition<*>) {
        val scopeDef = _scopeDefinitions[bean.scopeQualifier.value]
                ?: error("Undeclared scope definition for definition: $bean")
        scopeDef.save(bean)
        _scopes.values.filter { it._scopeDefinition == scopeDef }.forEach { it.loadDefinition(bean) }
    }

    private fun createScopeDefinition(qualifier: Qualifier) {
        val def = ScopeDefinition(qualifier)
        if (_scopeDefinitions[qualifier.value] == null) {
            _scopeDefinitions[qualifier.value] = def
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
        return _scopes[scopeId]
    }

    fun createScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null): Scope {
        if (_scopes.contains(scopeId)) {
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
        scope._scopeDefinition.removeExtras()
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
        module.definitions.forEach { bean ->
            val scopeDefinition = _scopeDefinitions[bean.scopeQualifier.value] ?: error(
                    "Can't find scope for definition $bean")
            scopeDefinition.unloadDefinition(bean)
            _scopes.values
                    .filter { it._scopeDefinition.qualifier == scopeDefinition.qualifier }
                    .forEach { it.dropInstance(bean) }
        }
        module.isLoaded = false
    }

}