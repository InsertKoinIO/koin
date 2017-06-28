package org.koin.context

import kotlin.reflect.KClass

/**
 * Created by arnaud on 27/06/2017.
 */
data class Scope(val name: String, val parent: Scope? = null) {

    fun isRoot() = this == root()

    companion object {
        fun root() = Scope("Root")
        fun fromClass(clazz: KClass<*>) = Scope(clazz.java.simpleName, Companion.root())
    }
}