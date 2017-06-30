package org.koin.context

import kotlin.reflect.KClass

/**
 * Created by arnaud on 27/06/2017.
 */
data class Scope(val clazz: KClass<*>? = null) {

    fun isRoot() = clazz == null

    override fun toString(): String = if (isRoot()) "Scope[$clazz]" else "Scope(ROOT)"

    companion object {
        fun root() = Scope()
    }
}