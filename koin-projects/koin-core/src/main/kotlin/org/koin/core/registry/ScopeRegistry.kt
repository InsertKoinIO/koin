package org.koin.core.registry

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeId

class ScopeRegistry {

    private val allScopes = hashSetOf<Scope>()
    private val registeredScopes = hashMapOf<String, Scope>()

    fun getOrCreateScope(scopeId: String): Scope {
        return getScopeById(scopeId) ?: createScope(scopeId)
    }

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

    fun detachScope(scopeId: String): Scope {
        return createNewScope(scopeId)
    }

    fun getScopeByInternalId(internalId: String): Scope? {
        return allScopes.firstOrNull { it.internalId == internalId }
    }

    private fun createNewScope(scopeId: String): Scope {
        val newScope = Scope(scopeId)
        allScopes.add(newScope)
        return newScope
    }

    private fun isNotAlreadyRegistered(scopeId: String) = registeredScopes[scopeId] == null

    fun getScopeById(scopeId: String): Scope? {
        return registeredScopes[scopeId]
    }

    internal fun deleteScope(scope: Scope) {
        allScopes.remove(scope)
        if (registeredScopes[scope.id] == scope) {
            registeredScopes.remove(scope.id)
        }
    }

    fun prepareScope(definition: BeanDefinition<*>, scope: Scope? = null): Scope? {
        return if (definition.isScoped()) {
            if (scope == null) {
                val scopeId = definition.getScopeId() ?: error("No scope id for $definition")
                getScopeById(scopeId)
                        ?: throw ScopeNotCreatedException("Scope '$scopeId' is not created while trying to use scoped definition: $definition")
            } else scope
        } else null
    }
}