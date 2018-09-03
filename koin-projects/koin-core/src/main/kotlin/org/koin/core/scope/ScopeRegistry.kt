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
package org.koin.core.scope

import org.koin.core.Koin

/**
 * Scope Registry
 * Coordinates all scopes
 */
class ScopeRegistry {

    private val scopes = HashMap<String, Scope>()
    private val scopeCallbacks: ArrayList<ScopeCallback> = arrayListOf()


    fun getOrCreateScope(id: String): Scope {
        var found = getScope(id)
        if (found == null) {
            found = Scope(id, this)
            scopes[id] = found
            Koin.logger.info("[Scope] create $id")
        }
        return found
    }

    fun createScope(id: String): Scope {
        var found = getScope(id)
        if (found == null) {
            found = Scope(id, this)
            scopes[id] = found
            Koin.logger.info("[Scope] create $id")
        } else {
            error("Already created scope with id '$id'")
        }
        return found
    }

    fun getScope(id: String) = scopes[id]

    fun closeScope(scope: Scope) {
        val id = scope.id
        scopes.remove(id)
        // callback
        scopeCallbacks.forEach { it.onClose(id) }
    }

    fun close() {
        scopes.values.forEach { it.holders.clear() }
        scopes.clear()
        Koin.logger.debug("[Close] Closing all scopes")
    }

    fun register(callback: ScopeCallback) {
        Koin.logger.info("[Scope] callback registering with $callback")
        scopeCallbacks += callback
    }
}