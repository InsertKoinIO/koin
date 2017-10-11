package org.koin.core.scope

/**
 * Context Scope
 * Help define logical separation for group of definitions
 * @author Arnaud GIULIANI
 */
data class Scope(val name: String, val parent: Scope? = null) {

    companion object {
        val ROOT = "ROOT"
        fun root() = Scope(ROOT)
    }
}