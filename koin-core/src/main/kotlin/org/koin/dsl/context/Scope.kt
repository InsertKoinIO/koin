package org.koin.dsl.context

import kotlin.reflect.KClass

/**
 * Created by arnaud on 27/06/2017.
 */
data class Scope(val clazz: KClass<*>) {

    companion object {
        fun root() = Scope(RootScope::class)
    }
}

class RootScope