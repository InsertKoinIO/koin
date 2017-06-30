package org.koin.context

import kotlin.reflect.KClass

/**
 * Created by arnaud on 27/06/2017.
 */
data class Scope(val clazz: KClass<*>? = null) {

    fun isRoot() = this == root()

    companion object {
        fun root() = Scope()
        fun fromClass(clazz: KClass<*>) = Scope(clazz)
    }
}