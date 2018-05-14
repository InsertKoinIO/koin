package org.koin.core.path

import org.koin.core.Koin
import org.koin.dsl.path.ModulePath
import org.koin.error.NoScopeFoundException

/**
 * Create & Handle all module paths
 *
 * @author Arnaud GIULIANI
 */
class ModulePathRegistry {
    val paths = hashSetOf<ModulePath>()
    private val root = ModulePath.root()

    init {
        paths += root
    }

    /**
     * Retrieve ModulePath for given path
     */
    fun getPath(path: String): ModulePath {
        return if (path == "") ModulePath.root()
        else {
            val paths = path.split(".")
            var moduleModulePath: ModulePath? = null
            paths.forEach { current -> moduleModulePath = this.paths.firstOrNull { it.name == current } }
            moduleModulePath ?: throw NoScopeFoundException("no module path found for '$path'")
        }
    }

    /**
     * Find or create ModulePath for given path & parentPath
     * @param path
     * @param parentPath
     */
    private fun findOrCreatePath(path: String?, parentPath: String? = null): ModulePath {
        return if (path == null) root
        else {
            paths.firstOrNull { it.name == path } ?: createScope(path, parentPath)
        }
    }

    /**
     * create ModulePath for given path & parentPath
     * @param path
     * @param parentPath
     */
    private fun createScope(scope: String, parentScope: String?): ModulePath {
        val parentLog = if (parentScope != null) "with parent [$parentScope]" else ""
        Koin.logger.debug("[scope] create [$scope] $parentLog")
        val s = ModulePath(scope, parent = findOrCreatePath(parentScope))
        paths += s
        return s
    }

    /**
     * Make ModulePath from path
     */
    fun makePath(path: String, parentPath: String? = null): ModulePath {
        val paths = path.split(".")
        var parent: String? = parentPath
        var createdModulePath: ModulePath = ModulePath.root()
        paths.forEach {
            createdModulePath = findOrCreatePath(it, parent)
            parent = it
        }
        return createdModulePath
    }

    /**
     * Retrieve paths (with children paths)
     */
    fun getAllPathsFrom(path: String): Set<ModulePath> {
        val scope = getPath(path)
        val firstChild = paths.filter { it.parent == scope }
        return setOf(scope) + firstChild + firstChild.flatMap { getAllPathsFrom(it.name) }
    }

    fun clear() {
        paths.clear()
        paths += root
    }
}