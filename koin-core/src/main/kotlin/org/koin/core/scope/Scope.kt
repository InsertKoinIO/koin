package org.koin.core.scope

/**
 * ModuleDefinition Scope - Define hierarchy between contexts
 * Help define logical separation for group of definitions
 *
 *
 * @param name - Scope name
 * @param parent - parent scope
 *
 * @author Arnaud GIULIANI
 */
data class Scope(val name: String, val parent: Scope? = null) {

    companion object {
        val ROOT = "ROOT"
        fun root() = Scope(ROOT)
    }

    override fun toString(): String {
        val p = if (parent != null) ", parent = $parent" else ""
        return "Scope[$name$p]"
    }

    fun isVisible(p: Scope): Boolean = this == p || if (p.parent != null) {
        isVisible(p.parent)
    } else false

    override fun equals(other: Any?): Boolean {
        return if (other is Scope) {
            name == other.name
        } else false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}