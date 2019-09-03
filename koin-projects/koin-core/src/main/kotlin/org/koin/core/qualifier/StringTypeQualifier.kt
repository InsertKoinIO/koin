package org.koin.core.qualifier

import org.koin.ext.getFullName
import kotlin.reflect.KClass

data class StringTypeQualifier(val type: KClass<*>, val value: String) : Qualifier {
    override fun toString(): String {
        return "$value:${type.getFullName()}"
    }
}
