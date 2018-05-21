package org.koin.dsl.path

/**
 * Path to localise a Module
 *
 * @author Arnaud Giuliani
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