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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier._q
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.mp.KoinPlatformTools.safeHashMap
import org.koin.mp.KoinPlatformTools.safeSet

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
class ScopeRegistry(private val _koin: Koin) {

    private val _scopeDefinitions = safeSet<Qualifier>()
    val scopeDefinitions: Set<Qualifier>
        get() = _scopeDefinitions

    private val _scopes = safeHashMap<ScopeID, Scope>()

    @KoinInternalApi
    val rootScope = Scope(rootScopeQualifier, ROOT_SCOPE_ID, isRoot = true, _koin = _koin)

    init {
        _scopeDefinitions.add(rootScope.scopeQualifier)
        _scopes[rootScope.id] = rootScope
    }

    @PublishedApi
    internal fun getScopeOrNull(scopeId: ScopeID): Scope? {
        return _scopes[scopeId]
    }

    @PublishedApi
    internal fun createScope(scopeId: ScopeID, qualifier: Qualifier, source: Any? = null,scopeArchetype : TypeQualifier? = null): Scope {
        _koin.logger.debug("| (+) Scope - id:'$scopeId' q:'$qualifier'")
        if (!_scopeDefinitions.contains(qualifier)) {
            _koin.logger.debug("| Scope '$qualifier' not defined. Creating it ...")
            _scopeDefinitions.add(qualifier)
        }
        if (_scopes.contains(scopeId)) {
            throw ScopeAlreadyCreatedException("Scope with id '$scopeId' is already created")
        }
        val scope = Scope(qualifier, scopeId, _koin = _koin, scopeArchetype = scopeArchetype)
        source?.let {
            _koin.logger.debug("|- Scope source set id:'$scopeId' -> $source")
            scope.sourceValue = source
        }
        scope.linkTo(rootScope)
        _scopes[scopeId] = scope
        return scope
    }

    internal fun deleteScope(scopeId: ScopeID) {
        _scopes[scopeId]?.let { deleteScope(it) }
    }

    internal fun deleteScope(scope: Scope) {
        _koin.instanceRegistry.dropScopeInstances(scope)
        _scopes.remove(scope.id)
    }

    internal fun close() {
        closeAllScopes()
        _scopes.clear()
        _scopeDefinitions.clear()
    }

    private fun closeAllScopes() {
        _scopes.values.toTypedArray().forEach { scope ->
            scope.close()
        }
    }

    fun loadScopes(modules: Set<Module>) {
        modules.forEach {
            loadModule(it)
        }
    }

    private fun loadModule(module: Module) {
        _scopeDefinitions.addAll(module.scopes)
    }

    companion object {
        private const val ROOT_SCOPE_ID = "_root_"

        @PublishedApi
        internal val rootScopeQualifier = _q(ROOT_SCOPE_ID)
    }
}
