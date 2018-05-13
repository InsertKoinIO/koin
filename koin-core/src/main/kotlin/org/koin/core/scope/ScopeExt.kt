package org.koin.core.scope

import org.koin.standalone.getContext
import kotlin.reflect.KClass

/**
 * Get scope for KClass
 *
 * i.e: org.koin.Foo class scope is 'org.koin'
 */
val <T : Any> KClass<T>.scope: Scope
    get() = getContext().scopeRegistry.getScope(getScopePath(this))

/**
 * Get scope from current class
 * @see KClass.scope
 */
val Any.scope get() = this::class.scope

/**
 * Compute scope path for given KClass
 *
 * i.e: org.koin.Foo class is 'org.koin'
 */
fun getScopePath(clazz: KClass<*>): String {
    val split = clazz.java.canonicalName.split(".")
    return split.subList(0, split.size - 1).joinToString(separator = ".")
}
