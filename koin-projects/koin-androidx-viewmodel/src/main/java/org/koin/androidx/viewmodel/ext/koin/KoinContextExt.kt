package org.koin.androidx.viewmodel.ext.koin

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.NoBeanDefFoundException

/**
 * Retrieve an instance by its class canonical name
 */
fun <T> KoinContext.getByTypeName(canonicalName: String, module: String? = null, parameters: ParameterDefinition): T {
    val foundDefinitions =
        beanRegistry.definitions.filter { it.clazz.java.canonicalName == canonicalName }.distinct()
    return getWithDefinitions(foundDefinitions, module, parameters, "for class name '$canonicalName'")
}

/**
 * Retrieve an instance by its bean beanDefinition name
 */
fun <T> KoinContext.getByName(name: String, module: String? = null, parameters: ParameterDefinition): T {
    val foundDefinitions = beanRegistry.definitions.filter { it.name == name }.distinct()
    return getWithDefinitions(foundDefinitions, module, parameters, "for bean name '$name'")
}

/**
 * Retrieve bean beanDefinition instance from given definitions
 */
private fun <T> KoinContext.getWithDefinitions(
    foundDefinitions: List<BeanDefinition<*>>,
    module: String? = null,
    parameters: ParameterDefinition,
    message: String
): T {
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean beanDefinition found $message")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstance(module, def.clazz, parameters, { listOf(def) })
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found $message")
    }
}