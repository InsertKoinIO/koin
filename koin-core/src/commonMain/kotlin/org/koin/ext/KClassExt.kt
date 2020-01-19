package org.koin.ext

import kotlin.reflect.KClass

/**
 * Give full class qualifier
 */
expect fun KClass<*>.getFullName(): String

expect fun KClass<*>.saveCache(): String
