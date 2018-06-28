package org.koin.reflect

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.NoBeanDefFoundException

/**
 * Retrieve instance by type name
 *
 * @param canonicalName - type full name
 * @param module
 * @param parameters
 */
fun <T> KoinContext.getByCanonicalName(
    canonicalName: String,
    module: String? = null,
    parameters: ParameterDefinition,
    filter: DefinitionFilter? = null
): T {
    val definitions = if (filter != null) beanRegistry.definitions.filter(filter) else beanRegistry.definitions

    val foundDefinitions =
        definitions.filter { it.clazz.java.canonicalName == canonicalName || it.types.filter { it.java.canonicalName == canonicalName }.isNotEmpty() }
            .distinct()

    try {
        return resolveInstanceFromDefinitions(foundDefinitions, module, parameters)
    } catch (e: NoBeanDefFoundException) {
        throw NoBeanDefFoundException("Can't create instance for class '$canonicalName'")
    }
}

/**
 * Retrieve an instance by its bean beanDefinition name
 */
fun <T> KoinContext.getByTypeName(
    name: String,
    module: String? = null,
    parameters: ParameterDefinition,
    filter: DefinitionFilter? = null
): T {
    val definitions = if (filter != null) beanRegistry.definitions.filter(filter) else beanRegistry.definitions

    val foundDefinitions =
        definitions.filter { it.name == name || it.types.filter { it.java.simpleName == name }.isNotEmpty() }.distinct()
    try {
        return resolveInstanceFromDefinitions(foundDefinitions, module, parameters)
    } catch (e: Exception) {
        throw NoBeanDefFoundException("Can't create instance for class '$name'")
    }
}

/**
 * Resolve instance from
 */
private fun <T> KoinContext.resolveInstanceFromDefinitions(
    foundDefinitions: List<BeanDefinition<*>>,
    module: String?,
    parameters: ParameterDefinition
): T {
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean beanDefinition found")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstanceFromDefinitions(module, def.clazz, parameters) { listOf(def) }
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found")
    }
}