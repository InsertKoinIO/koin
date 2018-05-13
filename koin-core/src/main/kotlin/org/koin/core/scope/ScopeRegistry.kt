package org.koin.core.scope

import org.koin.core.Koin
import org.koin.error.NoScopeFoundException

class ScopeRegistry {
    val scopes = hashSetOf<Scope>()
    private val rootScope = Scope.root()

    init {
        scopes += rootScope
    }

    /**
     * Retrieve scope for given path
     */
    fun getScope(path: String): Scope {
        return if (path == "") Scope.root()
        else {
            val paths = path.split(".")
            var scope: Scope? = null
            paths.forEach { current -> scope = scopes.firstOrNull { it.name == current } }
            scope ?: throw NoScopeFoundException("no scope found for '$path'")
        }
    }

    /**
     * Find or create context scope
     */
    private fun findOrCreateScope(scopeName: String?, parentScopeName: String? = null): Scope {
        return if (scopeName == null) rootScope
        else {
            scopes.firstOrNull { it.name == scopeName } ?: createScope(scopeName, parentScopeName)
        }
    }

    /**
     * Create context scope
     */
    private fun createScope(scope: String, parentScope: String?): Scope {
        val parentLog = if (parentScope != null) "with parent [$parentScope]" else ""
        Koin.logger.log("[scope] create [$scope] $parentLog")
        val s = Scope(scope, parent = findOrCreateScope(parentScope))
        scopes += s
        return s
    }

    /**
     * Make scope from path
     */
    fun makeScope(path: String, parentPath: String? = null): Scope {
        val paths = path.split(".")
        var parent: String? = parentPath
        var createdScope: Scope = Scope.root()
        paths.forEach {
            createdScope = findOrCreateScope(it, parent)
            parent = it
        }
        return createdScope
    }

    /**
     * Retrieve scopes (with children scope)
     */
    fun getAllScopesFrom(path: String): Set<Scope> {
        val scope = getScope(path)
        val firstChild = scopes.filter { it.parent == scope }
        return setOf(scope) + firstChild + firstChild.flatMap { getAllScopesFrom(it.name) }
    }

    fun clear() {
        scopes.clear()
        scopes += rootScope
    }
}