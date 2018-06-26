package org.koin.androidx.architecture.ext

import androidx.lifecycle.ViewModel
import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.Context
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

/**
 * ViewModel DSL Extension
 * Allow to declare a vieModel - be later inject into Activity/Fragment with dedicated injector
 */
inline fun <reified T : ViewModel> Context.viewModel(
        name: String = "",
        noinline definition: Definition<T>
) {
    val bean = factory(name, definition)
    bean.bind(ViewModel::class)
}

/**
 * Retrieve an instance by its class canonical name
 */
fun <T> KoinContext.getByTypeName(canonicalName: String, parameters: Parameters): T {
    val foundDefinitions =
            beanRegistry.definitions.filter { it.clazz.java.canonicalName == canonicalName }.distinct()
    return getWithDefinitions(foundDefinitions, parameters, "for class name '$canonicalName'")
}

/**
 * Retrieve an instance by its bean definition name
 */
fun <T> KoinContext.getByName(name: String, parameters: Parameters): T {
    val foundDefinitions = beanRegistry.definitions.filter { it.name == name }.distinct()
    return getWithDefinitions(foundDefinitions, parameters, "for bean name '$name'")
}

/**
 * Retrieve bean definition instance from given definitions
 */
private fun <T> KoinContext.getWithDefinitions(
        foundDefinitions: List<BeanDefinition<*>>,
        parameters: Parameters,
        message: String
): T {
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean definition found $message")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstance(def.clazz, parameters, { listOf(def) })
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found $message")
    }
}

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.get(modelClass: Class<T>, parameters: Parameters): T =
        (StandAloneContext.koinContext as KoinContext).getByTypeName(
                modelClass.canonicalName,
                parameters
        )

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.getByName(name: String, parameters: Parameters): T =
        (StandAloneContext.koinContext as KoinContext).getByName(name, parameters)