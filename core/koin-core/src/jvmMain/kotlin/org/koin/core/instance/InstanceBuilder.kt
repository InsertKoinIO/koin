@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult
import org.koin.ext.getFullName
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

@KoinReflectAPI
@Deprecated("Koin Reflection API is deprecated")
inline fun <reified T : Any> Scope.newInstance(defParams: ParametersHolder = emptyParametersHolder()): T {
    return newInstance(T::class, defParams)
}

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
@KoinReflectAPI
@Deprecated("Koin Reflection API is deprecated")
fun <T : Any> Scope.newInstance(kClass: KClass<T>, params: ParametersHolder): T {
    val instance: Any

    if (logger.level == Level.DEBUG) {
        logger.debug("|- creating new instance - ${kClass.getFullName()}")
    }

    val constructor = kClass.java.constructors.firstOrNull()
        ?: error("No constructor found for class '${kClass.getFullName()}'")

    val args = if (logger.level == Level.DEBUG) {
        val (_args, duration) = measureDurationForResult {
            getArguments(constructor, this, params)
        }
        logger.debug("|- got arguments in $duration ms")
        _args
    } else {
        getArguments(constructor, this, params)
    }

    instance = if (logger.level == Level.DEBUG) {
        val (_instance, duration) = measureDurationForResult {
            createInstance(args, constructor)
        }
        logger.debug("|- created instance in $duration ms")
        _instance
    } else {
        createInstance(args, constructor)
    }
    return instance as T
}

private fun createInstance(args: Array<Any>, constructor: Constructor<out Any>): Any {
    return if (args.isEmpty()) {
        constructor.newInstance()
    } else {
        constructor.newInstance(*args)
    }
}

/**
 * Retrieve arguments for given constructor
 */
private fun getArguments(constructor: Constructor<*>, scope: Scope, parameters: ParametersHolder): Array<Any> {
    val length = constructor.parameterTypes.size
    return if (length == 0) {
        emptyArray()
    } else {
        val result = Array<Any>(length) {}
        for (i in 0 until length) {
            val p = constructor.parameterTypes[i]
            val parameterClass = p.kotlin
            result[i] = scope.getOrNull(parameterClass, null) { parameters } ?: parameters.getOrNull(parameterClass) ?: throw NoBeanDefFoundException("No definition found for class '$parameterClass'")
        }
        result
    }
}
