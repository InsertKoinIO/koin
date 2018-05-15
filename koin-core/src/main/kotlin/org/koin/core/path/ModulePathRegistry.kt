package org.koin.core.path

import org.koin.core.Koin
import org.koin.dsl.path.Path
import org.koin.error.NoModulePathException

/**
 * Create & Handle all module paths
 *
 * @author Arnaud GIULIANI
 */
class ModulePathRegistry {
    val paths = hashSetOf<Path>()

    private val root = Path.root()

    init {
        paths += root
    }

    /**
     * Retrieve ModulePath for given path
     * @param path
     */
    fun getPath(path: String): Path {
        return if (path == Path.ROOT) root
        else {
            val paths = path.split(".")
            var modulePath: Path? = null
            paths.forEach { current -> modulePath = this.paths.firstOrNull { it.name == current } }
            modulePath ?: throw NoModulePathException("no module path found for '$path'")
        }
    }

    /**
     * Make ModulePath from path
     * @param path
     * @param parentPath
     */
    fun makePath(path: String, parentPath: String? = null): Path {
        if (parentPath != null) {
            Koin.logger.debug("[module] path [$parentPath.$path] ")
        } else {
            Koin.logger.debug("[module] path [$path] ")
        }
        return if (path == Path.ROOT) root
        else {
            val completePath = if (!parentPath.isNullOrEmpty()) "$parentPath.$path" else path
            val paths = completePath.split(".")
            val modulePath = paths.fold(root, { acc: Path, s: String ->
                Path(s, acc)
            })
            savePath(modulePath)
            return modulePath
        }
    }

    /**
     * Save path
     */
    private fun savePath(path: Path) {
        paths.add(path)
        path.parent?.let {
            savePath(it)
        }
    }

    /**
     * Retrieve paths (with children paths)
     * @param path
     */
    fun getAllPathsFrom(path: String): Set<Path> {
        val mainPath = getPath(path)
        val firstChild = paths.filter { it.parent == mainPath }
        return setOf(mainPath) + firstChild + firstChild.flatMap { getAllPathsFrom(it.name) }
    }

    fun clear() {
        paths.clear()
        paths += root
    }
}