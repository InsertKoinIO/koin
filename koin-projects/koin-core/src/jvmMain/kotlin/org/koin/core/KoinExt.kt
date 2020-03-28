package org.koin.core

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Get a Koin instance
 * @param java class
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T
 */
@JvmOverloads
fun <T> Koin.get(
        clazz: Class<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
    val kClass = clazz.kotlin
    return get(kClass, qualifier, parameters)
}

/**
 * Get a Koin instance
 * @param java class
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T
 */
@JvmOverloads
fun <T> Koin.get(
        clazz: KClass<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
    return get(clazz, qualifier, parameters)
}