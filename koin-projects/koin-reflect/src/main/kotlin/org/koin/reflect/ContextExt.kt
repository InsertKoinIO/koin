package org.koin.reflect

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.dsl.context.ModuleDefinition
import org.koin.reflect.error.NoConstructorFound
import org.koin.standalone.StandAloneContext

/**
 * Build instance with Koin injected dependencies
 */
inline fun <reified T : Any> ModuleDefinition.build(): T {
    val clazz = T::class.java
    val ctor = clazz.constructors.firstOrNull() ?: throw NoConstructorFound("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { getFromClass(it) }.toTypedArray()
    return ctor.newInstance(*args) as T
}

/**
 * Get instance for clazz
 *
 * @param clazz : Class
 * @param module
 * @param parameters
 */
fun <T> ModuleDefinition.getFromClass(
    clazz: Class<T>,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T = (StandAloneContext.koinContext as KoinContext).getByCanonicalName(
    clazz.canonicalName,
    module,
    parameters
)