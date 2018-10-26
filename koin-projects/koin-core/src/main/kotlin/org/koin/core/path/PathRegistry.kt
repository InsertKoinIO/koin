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
package org.koin.core.path

import org.koin.dsl.path.Path
import org.koin.error.BadPathException

/**
 * Create & Handle all module paths
 *
 * @author - Arnaud GIULIANI
 */
class PathRegistry {
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
            modulePath ?: throw BadPathException("no module path found for '$path'")
        }
    }

    /**
     * Make ModulePath from path
     * @param path
     * @param parentPath
     */
    fun makePath(path: String, parentPath: String? = null): Path {
        return if (path == Path.ROOT) root
        else {
            val completePath = if (!parentPath.isNullOrEmpty()) "$parentPath.$path" else path
            val paths = completePath.split(".")
            val modulePath = paths.fold(root) { acc: Path, s: String ->
                Path(s, acc)
            }
            return modulePath
        }
    }

    /**
     * Save path
     */
    fun savePath(path: Path) {
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