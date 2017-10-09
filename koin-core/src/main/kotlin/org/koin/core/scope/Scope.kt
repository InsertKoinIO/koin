package org.koin.core.scope

/**
 *
 */
data class Scope(val name: String, val parent: Scope? = null) {

    companion object {
        val ROOT = "ROOT"
        fun root() = Scope(ROOT)
    }
}