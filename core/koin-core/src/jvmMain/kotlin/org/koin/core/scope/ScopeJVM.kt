package org.koin.core.scope

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Get a Koin instance
 * @param java class
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T
 */
@JvmOverloads
fun <T : Any> Scope.get(
    clazz: Class<*>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    val kClass = clazz.kotlin
    return get(kClass, qualifier, parameters)
}