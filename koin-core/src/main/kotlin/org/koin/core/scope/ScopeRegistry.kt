package org.koin.core.scope

import org.koin.core.Koin
import org.koin.error.NoScopeFoundException

class ScopeRegistry {
    val scopes = arrayListOf<Scope>()
    private val rootScope = Scope.root()

    init {
        scopes += rootScope
    }

    /**
     * Retrieve context scope for given name
     */
    fun getScope(name: String): Scope = scopes.firstOrNull { it.name == name }
            ?: throw NoScopeFoundException("ModuleDefinition scope '$name' not found")

    /**
     * Find or create context scope
     */
    fun findOrCreateScope(scopeName: String?, parentScopeName: String? = null): Scope {
        return if (scopeName == null) rootScope
        else {
            scopes.firstOrNull { it.name == scopeName } ?: createScope(scopeName, parentScopeName)
        }
    }

    /**
     * Create context scope
     */
    fun createScope(scope: String, parentScope: String?): Scope {
        Koin.logger.log("[scope] create [$scope] with parent [$parentScope]")
        val s = Scope(scope, parent = findOrCreateScope(parentScope))
        scopes += s
        return s
    }

    /**
     * Retrieve scope and child for given name
     */
    fun allScopesfrom(name: String): List<Scope> {
        val scope = getScope(name)
        val firstChild = scopes.filter { it.parent == scope }
        return listOf(scope) + firstChild + firstChild.flatMap { allScopesfrom(it.name) }
    }

    fun clear() {
        scopes.clear()
    }
}