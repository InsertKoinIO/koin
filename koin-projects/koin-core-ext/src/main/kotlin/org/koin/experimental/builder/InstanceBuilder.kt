package org.koin.experimental.builder

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.module.Module
import org.koin.core.scope.ScopeInstance
import org.koin.core.time.measureDuration
import java.lang.reflect.Constructor
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> Module.create(scope: ScopeInstance? = null): T {
    val kClass = T::class
    val kclassAsString = kClass.toString()
    logger.debug("| autocreate '$kClass'")

    val (ctor, ctorDuration) = measureDuration {
        kClass.getFirstJavaConstructor()
    }
    logger.debug("| got ctor '$kclassAsString' in '$ctorDuration'")

    val (args, argsDuration) = measureDuration {
        getArguments(ctor, scope)
    }
    logger.debug("| got args '$kclassAsString' in '$argsDuration'")

    val (instance, instanceDuration) = measureDuration {
        ctor.makeInstance<T>(args)
    }
    logger.debug("| got instance '$kclassAsString' in '$instanceDuration'")

    return instance
}

/**
 * Make an instance with given arguments
 */
inline fun <reified T : Any> Constructor<*>.makeInstance(args: Array<Any>) =
    newInstance(*args) as T

/**
 * Retrieve arguments for given constructor
 */
fun Module.getArguments(ctor: Constructor<*>, scope: ScopeInstance?) =
    ctor.parameterTypes.map { getWithDefault(it.kotlin, scope) }.toTypedArray()

/**
 * Get first java constructor
 */
fun KClass<*>.getFirstJavaConstructor(): Constructor<*> {
    return allConstructors[this] ?: saveConstructor()
}

/**
 * Extract constructor and save it to constructors index
 */
fun KClass<*>.saveConstructor(): Constructor<*> {
    val clazz = this.java
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    allConstructors[this] = ctor
    return ctor
}

val allConstructors = ConcurrentHashMap<KClass<*>, Constructor<*>>()

/**
 * Retrieve linked dependency with defaults params
 */
internal fun <T : Any> Module.getWithDefault(
    clazz: KClass<T>,
    scope: ScopeInstance?
): T = koin.get(clazz, null, scope, null)