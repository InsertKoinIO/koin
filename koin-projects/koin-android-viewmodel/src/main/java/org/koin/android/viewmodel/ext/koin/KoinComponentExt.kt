package org.koin.android.viewmodel.ext.koin

import org.koin.android.viewmodel.ViewModelParameters
import org.koin.core.CustomRequest
import org.koin.core.parameter.ParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.getWith

/**
 * Create instance with parameters for ViewModelFactory
 * @see implemented ViewModelFactory
 *
 * @param p - ViewModelParameters
 * @param clazz - class
 */
fun <T> KoinComponent.createInstance(p: ViewModelParameters, clazz: Class<T>): T {
    // Get params to pass to factory
    val name = p.name
    val module = p.module
    val params = p.parameters
    // Clear local stuff

    return if (name != null) {
        retrieveViewModel(clazz, name, module, params)
    } else retrieveViewModel(clazz, module, params)
}

/**
 * Retrieve ViewModel Instance from class/name
 */
internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    name: String,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(isViewModel).filter { registry.filterByNameAndClass(it, name, clazz) }.distinct()
}, module, clazz, params))

/**
 * Retrieve ViewModel Instance from class
 */
internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    module: String?,
    params: ParameterDefinition
): T = getWith(CustomRequest({ registry ->
    registry.definitions.filter(isViewModel).filter { registry.filterByClass(it, clazz) }.distinct()
}, module, clazz, params))