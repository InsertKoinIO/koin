package org.koin.core

import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult
import org.koin.ext.getFullName
import org.koin.mp.NativeClass

/**
 * Get a Koin instance
 * @param java class
 * @param qualifier
 * @param parameters
 *
 * @return instance of type T
 */
fun <T> Scope.get(
    clazz: NativeClass<*>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    val kClass = clazz.kotlin
    return if (_koin._logger.isAt(Level.DEBUG)) {
        _koin._logger.debug("+- get '${kClass.getFullName()}' with qualifier '$qualifier'")
        val (instance: T, duration: kotlin.Double) = measureDurationForResult {
            resolveInstance<T>(qualifier, kClass, parameters)
        }
        _koin._logger.debug("+- got '${kClass.getFullName()}' in $duration ms")
        return instance
    } else {
        resolveInstance(qualifier, kClass, parameters)
    }
}