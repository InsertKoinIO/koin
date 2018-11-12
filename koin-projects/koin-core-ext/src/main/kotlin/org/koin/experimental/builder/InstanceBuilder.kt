package org.koin.experimental.builder

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.module.Module
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> Module.create(): T {
    val kClass = T::class
    logger.debug("| autocreate '$kClass'")

    val ctor = kClass.getFirstJavaConstructor()
    val args = getArguments(ctor)
    return ctor.makeInstance(args)
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