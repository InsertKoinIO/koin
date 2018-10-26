package org.koin.ext

import kotlin.reflect.KClass

/**
 * Help Get/display name
 */
internal fun <T : Any> KClass<T>.name(): String = java.simpleName
/**
 * Help Get/display name
 */
internal fun <T : Any> KClass<T>.fullname(): String = java.canonicalName