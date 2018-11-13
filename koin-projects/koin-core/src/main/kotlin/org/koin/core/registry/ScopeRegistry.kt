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

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeId
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
class ScopeRegistry {

    private val allScopes = hashSetOf<Scope>()
    private val registeredScopes: MutableMap<String, Scope> = ConcurrentHashMap()

    /**
     * Get or create a scope for given Id
     * @param scopeId
     */
    fun getOrCreateScope(scopeId: String): Scope {
        return getScopeById(scopeId) ?: createScope(scopeId)
    }

    /**
     * Create a scope or throw ScopeAlreadyCreatedException if created
     * @param scopeId
     */
    fun createScope(scopeId: String): Scope {
        return if (isNotAlreadyRegistered(scopeId)) {
            val scope = createNewScope(scopeId)
            registerScope(scopeId, scope)
        } else {
            throw ScopeAlreadyCreatedException("Try to create scope '$scopeId' but is alreadyCreated")
        }
    }

    private fun registerScope(scopeId: String, scope: Scope): Scope {
        registeredScopes[scopeId] = scope
        return scope
    }

    /**
     * Detach a scope, i.e can't be found by getScopeById but by its internal id
     * @param scopeId
     */
    fun detachScope(scopeId: String): Scope {
        return createNewScope(scopeId)
    }

    /**
     * Retrieve a scope by its internal id (for detached scope)
     * @param internalId
     */
    fun getScopeByInternalId(internalId: String): Scope? {
        return allScopes.firstOrNull { it.internalId == internalId }
    }

    private fun createNewScope(scopeId: String): Scope {
        val newScope = Scope(scopeId)
        allScopes.add(newScope)
        return newScope
    }

    private fun isNotAlreadyRegistered(scopeId: String) = registeredScopes[scopeId] == null

    /**
     * Retrieve a scope by its id (scopeId)
     * @param scopeId
     */
    fun getScopeById(scopeId: String): Scope? {
        return registeredScopes[scopeId]
    }

    internal fun deleteScope(scope: Scope) {
        allScopes.remove(scope)
        if (registeredScopes[scope.id] == scope) {
            registeredScopes.remove(scope.id)
        }
    }

    /**
     * Prepare scope for given definition
     * @param definition
     * @param scope
     */
    fun prepareScope(definition: BeanDefinition<*>, scope: Scope? = null): Scope? {
        return if (definition.isScoped()) {
            if (scope == null) {
                val scopeId = definition.getScopeId() ?: error("No scope id for $definition")
                getScopeById(scopeId)
                        ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created while trying to use scoped definition: $definition")
            } else scope
        } else null
    }

    fun close() {
        allScopes.clear()
        registeredScopes.clear()
    }
}