package org.koin.core.registry

import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.scope.Scope

class ScopeRegistry {

    private val allScopes = hashSetOf<Scope>()

    fun getOrCreateScope(scopeId: String): Scope {
        return getScope(scopeId) ?: createNewScope(scopeId)
    }

    fun createScope(scopeId: String): Scope {
        return if (isNotAlreadyCreated(scopeId)) {
            createNewScope(scopeId)
        } else {
            throw ScopeAlreadyCreatedException("Try to create scope '$scopeId' but is alreadyCreated")
        }
    }

    private fun createNewScope(scopeId: String): Scope {
        val newScope = Scope(scopeId)
        allScopes.add(newScope)
        return newScope
    }

    private fun isNotAlreadyCreated(scopeId: String) = allScopes.none { it.id == scopeId }

    fun getScope(scopeId: String): Scope? {
        return allScopes.firstOrNull { it.id == scopeId }
    }

    internal fun deleteScope(id: String) {
        allScopes.removeAll { it.id == id }
    }
}