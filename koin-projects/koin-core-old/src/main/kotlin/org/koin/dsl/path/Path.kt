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
package org.koin.dsl.path


/**
 * Path to localise a Module
 *
 * @author - Arnaud GIULIANI
 */
data class Path(val name: String, val parent: Path? = null) {

    fun isVisible(p: Path): Boolean = this == p || if (p.parent != null) isVisible(p.parent) else false

    override fun toString(): String {
        val parentPath = parent?.toString() ?: ""
        return if (parentPath.isNotEmpty()) "$parentPath.$name" else name
    }

    companion object {
        const val ROOT = ""
        fun root() = Path(ROOT)
    }
}