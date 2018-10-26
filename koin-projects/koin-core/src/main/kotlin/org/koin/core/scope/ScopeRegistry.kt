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
import org.koin.error.ScopeAlreadyExistsException

/**
 * Scope Registry
 * Coordinates all registeredScopes
 */
class ScopeRegistry {

    private val registeredScopes = HashMap<String, Scope>()
    private val allScopes = HashMap<String, Scope>()
    private val scopeCallbacks: ArrayList<ScopeCallback> = arrayListOf()

    fun getOrCreateScope(id: String): Scope {
        var found = getScope(id)
        if (found == null) {
            found = createAndRegisterScope(id)
        }
        return found
    }

    private fun createAndRegisterScope(
        id: String
    ): Scope {
        val scope = Scope(id)
        registerScope(scope)
        saveScope(scope)
        Koin.logger.debug("[Scope] declare $scope")
        return scope
    }

    private fun registerScope(scope: Scope) {
        registeredScopes[scope.id] = scope
    }

    private fun saveScope(scope: Scope) {
        allScopes[scope.uuid] = scope
    }

    fun createScope(id: String): Scope {
        var found = getScope(id)
        if (found == null) {
            found = createAndRegisterScope(id)
        } else {
            throw ScopeAlreadyExistsException("Scope id '$id' is already created")
        }
        return found
    }

    fun getScope(id: String): Scope? = registeredScopes[id]

    fun getDetachScope(uuid: String): Scope? = allScopes[uuid]

    fun createAndDetachScope(id: String): Scope {
        val scope = Scope(id, isDetached = true)
        saveScope(scope)
        Koin.logger.debug("[Scope] detached $scope")
        return scope
    }

    fun close() {
        val all = registeredScopes.values + allScopes.values
        all.forEach { it.close() }

        registeredScopes.clear()
        allScopes.clear()
    }

    fun register(callback: ScopeCallback) {
        scopeCallbacks += callback
    }

    fun deleteScope(id: String, uuid: String) {
        registeredScopes.remove(id)
        allScopes.remove(uuid)

        scopeCallbacks.forEach {
            it.onClose(id, uuid)
        }
    }
}