package org.koin.core.instance

import org.koin.core.logger.Level
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult
import org.koin.ext.getFullName
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

inline fun <reified T : Any> Scope.newInstance(): T {
    return newInstance(T::class)
}
/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
fun <T : Any> Scope.newInstance(kClass: KClass<T>): T {
    val instance: Any

    if (logger.level == Level.DEBUG) {
        logger.debug("!- creating class:${kClass.getFullName()}")
    }

    val constructor = kClass.java.constructors.firstOrNull()
        ?: error("No constructor found for class '${kClass.getFullName()}'")

    val args = if (logger.level == Level.DEBUG) {
        val (_args, duration) = measureDurationForResult {
            getArguments(constructor, this)
        }
        logger.debug("!- got arguments in $duration ms")
        _args
    } else {
        getArguments(constructor, this)
    }

    instance = if (logger.level == Level.DEBUG) {
        val (_instance, duration) = measureDurationForResult {
            createInstance(args, constructor)
        }
        logger.debug("!- created instance in $duration ms")
        _instance
    } else {
        createInstance(args, constructor)
    }
    return instance as T
}

fun createInstance(args: Array<Any>, constructor: Constructor<out Any>): Any {
    return if (args.isEmpty()) {
        constructor.newInstance()
    } else {
        constructor.newInstance(*args)
    }
}

/**
 * Retrieve arguments for given constructor
 */
fun getArguments(constructor: Constructor<*>, context: Scope): Array<Any> {
    val length = constructor.parameterTypes.size
    return if (length == 0) emptyArray()
    else {
        val result = Array<Any>(length) {}
        for (i in 0 until length) {
            val p = constructor.parameterTypes[i]
            result[i] = context.get(p.kotlin, null, null)
        }
        result
    }
}