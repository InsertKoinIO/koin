package org.koin.core.scope

/**
 * Context Scope - Define hierarchy between contexts
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

    fun isVisible(p: Scope): Boolean = this == p || if (p.parent != null) {
        isVisible(p.parent)
    } else false
}