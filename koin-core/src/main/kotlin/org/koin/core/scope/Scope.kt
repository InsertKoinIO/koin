package org.koin.core.scope

import org.koin.core.instance.InstanceFactory
import kotlin.reflect.KClass

/**
 * Created by arnaud on 27/06/2017.
 */
data class Scope(val clazz: KClass<*>, var parentScope: Scope? = null) {

    val instanceFactory = InstanceFactory()

    companion object {
        fun root() = Scope(RootScope::class)
    }
}


/**
 * ROOT Scope
 */
sealed class RootScope

