package org.koin.core.path

import org.koin.core.Koin
import org.koin.dsl.path.ModulePath
import org.koin.error.NoModulePathException

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
     * @param path
     */
    fun getPath(path: String): ModulePath {
        return if (path == "") ModulePath.root()
        else {
            val paths = path.split(".")
            var moduleModulePath: ModulePath? = null
            paths.forEach { current -> moduleModulePath = this.paths.firstOrNull { it.name == current } }
            moduleModulePath ?: throw NoModulePathException("no module path found for '$path'")
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
            paths.firstOrNull { it.name == path } ?: createPath(path, parentPath)
        }
    }

    /**
     * create ModulePath for given path & parentPath
     * @param path
     * @param parentPath
     */
    private fun createPath(path: String, parentPath: String?): ModulePath {
        if (parentPath != null) {
            Koin.logger.debug("[module] create path [$parentPath.$path] ")
        } else {
            Koin.logger.debug("[module] create path [$path] ")
        }
        val s = ModulePath(path, parent = findOrCreatePath(parentPath))
        paths += s
        return s
    }

    /**
     * Make ModulePath from path
     * @param path
     * @param parentPath
     */
    fun makePath(path: String, parentPath: String? = null): ModulePath {
        if (parentPath != null) {
            Koin.logger.debug("[module] make path [$parentPath.$path] ")
        } else {
            Koin.logger.debug("[module] make path [$path] ")
        }
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
     * @param path
     */
    fun getAllPathsFrom(path: String): Set<ModulePath> {
        val mainPath = getPath(path)
        val firstChild = paths.filter { it.parent == mainPath }
        return setOf(mainPath) + firstChild + firstChild.flatMap { getAllPathsFrom(it.name) }
    }

    fun clear() {
        paths.clear()
        paths += root
    }
}