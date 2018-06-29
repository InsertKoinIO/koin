package org.koin.android.viewmodel

import org.koin.core.CustomRequest
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.getWith

fun <T> KoinComponent.createInstance(p: ViewModelParameters, modelClass: Class<T>): T {
    // Get params to pass to factory
    val name = p.name
    val module = p.module
    val params = p.parameters
    // Clear local stuff

    return if (name != null) {
        retrieveViewModel(modelClass, name, module, params)
    } else retrieveViewModel(modelClass, module, params)
}

internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    name: String,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(filterIsViewModel).filter { registry.filterByNameAndClass(it, name, clazz) }
}, module, clazz, params))

internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(filterIsViewModel).filter { registry.filterByClass(it, clazz) }
}, module, clazz, params))

internal val filterIsViewModel = { def: BeanDefinition<*> -> def.isViewModel() }