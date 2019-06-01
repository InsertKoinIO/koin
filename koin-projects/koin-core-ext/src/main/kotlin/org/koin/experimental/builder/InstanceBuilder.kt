package org.koin.experimental.builder

import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import java.lang.reflect.Constructor
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> create(context: Scope): T {

    val kClass = T::class
    lateinit var instance: T

    val ctor = kClass.getFirstJavaConstructor()
    val args = getArguments(ctor, context)
    instance = ctor.makeInstance(args)

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
fun getArguments(ctor: Constructor<*>, context: Scope) =
        ctor.parameterTypes.map { context.getWithDefault(it.kotlin) }.toTypedArray()

/**
 * Get first java constructor
 */
fun KClass<*>.getFirstJavaConstructor(): Constructor<*> {
    return allConstructors[this.getFullName()] ?: saveConstructor()
}

/**
 * Extract constructor and save it to constructors index
 */
fun KClass<*>.saveConstructor(): Constructor<*> {
    val clazz = this.java
    val ctor = clazz.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    allConstructors[this.getFullName()] = ctor
    return ctor
}

val allConstructors = ConcurrentHashMap<String, Constructor<*>>()

/**
 * Retrieve linked dependency with defaults params
 */
internal fun <T : Any> Scope.getWithDefault(
        clazz: KClass<T>
): T {
    return get(clazz, null, null)
            ?: error("Koin can't be null in scope context")
}