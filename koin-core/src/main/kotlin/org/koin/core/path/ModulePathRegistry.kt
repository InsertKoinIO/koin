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
        return if (path == ModulePath.ROOT) root
        else {
            val paths = path.split(".")
            var moduleModulePath: ModulePath? = null
            paths.forEach { current -> moduleModulePath = this.paths.firstOrNull { it.name == current } }
            moduleModulePath ?: throw NoModulePathException("no module path found for '$path'")
        }
    }

    /**
     * Make ModulePath from path
     * @param path
     * @param parentPath
     */
    fun makePath(path: String, parentPath: String? = null): ModulePath {
        if (parentPath != null) {
            Koin.logger.debug("[module] path [$parentPath.$path] ")
        } else {
            Koin.logger.debug("[module] path [$path] ")
        }
        return if (path == ModulePath.ROOT) root
        else {
            val completePath = if (!parentPath.isNullOrEmpty()) "$parentPath.$path" else path
            val paths = completePath.split(".")
            val modulePath = paths.fold(root, { acc: ModulePath, s: String ->
                ModulePath(s, acc)
            })
            savePath(modulePath)
            return modulePath
        }
    }

    /**
     * Save path
     */
    private fun savePath(modulePath: ModulePath) {
        paths.add(modulePath)
        modulePath.parent?.let {
            savePath(it)
        }
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