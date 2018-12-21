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

import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeUUID

/**
 * Scope Registry
 * create/find scopes for Koin
 *
 * @author Arnaud Giuliani
 */
class ScopeRegistry {

    private val allScopes = hashSetOf<Scope>()
//    private val registeredScopes: MutableMap<String, Scope> = ConcurrentHashMap()

//    /**
//     * Get or create a scope for given Id
//     * @param scopeId
//     */
//    fun getOrCreateScope(scopeId: String): Scope {
//        return getScopeById(scopeId) ?: createScope(scopeId)
//    }

    /**
     * Create a scope
     * @param scopeName
     */
    fun createScope(scopeId: String): Scope {
        val scope = Scope(scopeId)
        allScopes.add(scope)
        return scope
//        return if (isNotAlreadyRegistered(scopeName)) {
//            val scope = createNewScope(scopeName)
//            registerScope(scopeName, scope)
//        } else {
//            throw ScopeAlreadyCreatedException("Try to create scope '$scopeName' but is alreadyCreated")
//        }
    }

    fun getScopeById(scopeId: String): Scope {
        val values = allScopes.filter { it.id == scopeId }
        return when (values.size) {
            0 -> throw ScopeNotCreatedException("Scope $scopeId not found")
            1 -> values.first()
            else -> error("Scope Id $scopeId is used by several scopes: $values. Please getScopeByUUID instead")
        }
    }

    fun getScopeByUUId(scopeUUID: ScopeUUID): Scope {
        return allScopes.firstOrNull { it.uuid == scopeUUID }
            ?: throw ScopeNotCreatedException("Scope $scopeUUID not found")
    }

    fun deleteScope(scopeId: ScopeUUID) {
        allScopes.removeIf { it.uuid == scopeId }
    }

//    private fun registerScope(scopeId: String, scope: Scope): Scope {
//        registeredScopes[scopeId] = scope
//        return scope
//    }
//
//    /**
//     * Detach a scope, i.e can't be found by getScopeById but by its internal name
//     * @param scopeId
//     */
//    fun detachScope(scopeId: String): Scope {
//        return createNewScope(scopeId)
//    }
//
//    /**
//     * Retrieve a scope by its internal name (for detached scope)
//     * @param uuid
//     */
//    fun getScopeByInternalId(uuid: String): Scope? {
//        return allScopes.firstOrNull { it.uuid == uuid }
//    }
//
//    private fun createNewScope(scopeId: String): Scope {
//        val newScope = Scope(scopeId)
//        allScopes.add(newScope)
//        return newScope
//    }
//
//    private fun isNotAlreadyRegistered(scopeId: String) = registeredScopes[scopeId] == null
//
//    /**
//     * Retrieve a scope by its name (scopeId)
//     * @param scopeId
//     */
//    fun getScopeById(scopeId: String): Scope? {
//        return registeredScopes[scopeId]
//    }
//
//    internal fun deleteScope(scope: Scope) {
//        allScopes.remove(scope)
//        if (registeredScopes[scope.name] == scope) {
//            registeredScopes.remove(scope.name)
//        }
//    }
//
//    /**
//     * Prepare scope for given definition
//     * @param definition
//     * @param scope
//     */
//    fun prepareScope(definition: BeanDefinition<*>, scope: Scope? = null): Scope? {
//        return if (definition.isScoped()) {
//            if (scope == null) {
//                val scopeId = definition.getScopeKey() ?: error("No scope name for $definition")
//                getScopeById(scopeId)
//                        ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created while trying to use scoped definition: $definition")
//            } else scope
//        } else null
//    }

    fun close() {
        allScopes.clear()
//        registeredScopes.clear()
    }
}