package org.koin.core.scope

import org.koin.core.Koin

class ScopeRegistry {

    private val scopes = HashMap<String, Scope>()

    fun createScope(id: String): Scope {
        var found = getScope(id)
        if (found == null) {
            found = Scope(id, this)
            scopes[id] = found
            Koin.logger.debug("[Scope] create $id")
        }
        else{
            error("Already created scope with id '$id'")
        }
        return found
    }

    fun getScope(id: String) = scopes[id]

    fun closeScope(scope : Scope) {
        val id = scope.id
        scopes.remove(id)
    }

    fun close() {
        scopes.values.forEach { it.holders.clear() }
        scopes.clear()
        Koin.logger.debug("[Close] Closing all scopes")
    }
}