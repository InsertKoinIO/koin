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
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.core.scope.ScopeSet
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
class ScopeRegistry {

    private val definitions = ConcurrentHashMap<String, ScopeSet>()
    private val instances = ConcurrentHashMap<String, Scope>()

    internal fun loadScopes(modules: Iterable<Module>) {
        modules.forEach {
            declareScopes(it)
        }
    }

    fun loadDefaultScopes(koin: Koin) {
        koin.defaultScope.register(koin)
        saveInstance(koin.defaultScope)
    }

    private fun declareScopes(module: Module) {
        module.scopes.forEach {
            saveDefinition(it)
        }
    }

    private fun saveDefinition(scopeSet: ScopeSet) {
        val foundScopeSet: ScopeSet? = definitions[scopeSet.qualifier.toString()]
        if (foundScopeSet == null) {
            definitions[scopeSet.qualifier.toString()] = scopeSet
        } else {
            foundScopeSet.definitions.addAll(scopeSet.definitions)
        }
    }

    fun getScopeDefinition(scopeName: String): ScopeSet? = definitions[scopeName]

    /**
     * Create a scope instance for given scope
     * @param id - scope instance id
     * @param scopeName - scope qualifier
     */
    fun createScopeInstance(id: ScopeID, scopeName: Qualifier? = null): Scope {
        val definition: ScopeSet? = scopeName?.let {
            definitions[scopeName.toString()]
                    ?: throw NoScopeDefinitionFoundException("No scope definition found for scopeName '$scopeName'")
        }
        val instance = Scope(id)
        definition?.let { instance.set = it }
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
        instances.remove(id)
    }

    fun close() {
        definitions.clear()
        instances.values.forEach { it.close() }
        instances.clear()
    }
}