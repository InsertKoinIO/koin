package org.koin.core.scope

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Get a Koin instance
 * @param clazz
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T
 */
fun <T> Scope.get(
        clazz: Class<*>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T = get(clazz.kotlin, qualifier, parameters)