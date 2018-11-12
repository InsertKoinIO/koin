package org.koin.experimental.builder

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.module.Module
import org.koin.core.time.measureDuration
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> Module.create(): T {
    val kClass = T::class
    val kclassAsString = kClass.toString()
    logger.debug("| autocreate '$kClass'")

    val (ctor, ctorDuration) = measureDuration {
        kClass.getFirstJavaConstructor()
    }
    logger.debug("| got ctor '$kclassAsString' in '$ctorDuration'")

    val (args, argsDuration) = measureDuration {
        getArguments(ctor)
    }
    logger.debug("| got args '$kclassAsString' in '$argsDuration'")

    val (instance, instanceDuration) = measureDuration {
        ctor.makeInstance<T>(args)
    }
    logger.debug("| got instance '$kclassAsString' in '$instanceDuration'")

    return instance
}

inline fun <reified T : Any> Constructor<*>.makeInstance(args: Array<Any>) =
    newInstance(*args) as T

fun Module.getArguments(ctor: Constructor<*>) =
    ctor.parameterTypes.map { getWithDefault(clazz = it.kotlin) }.toTypedArray()

fun KClass<*>.getFirstJavaConstructor(): Constructor<*> {
    return allConstructors[this] ?: saveConstructor()
}

fun KClass<*>.saveConstructor(): Constructor<*> {
    val clazz = this.java
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    allConstructors[this] = ctor
    return ctor
}

val allConstructors = hashMapOf<KClass<*>, Constructor<*>>()

/**
 * Resolve a koincomponent from its class
 *
 * @param name
 * @param clazz - java class
 * @param parameters
 */
fun <T : Any> Module.getWithDefault(
    clazz: KClass<T>
): T = koin.get(clazz, null, null, null)