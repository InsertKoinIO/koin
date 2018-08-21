package org.koin.androidx.viewmodel.ext.koin

import org.koin.android.viewmodel.ViewModelParameters
import org.koin.core.parameter.ParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.getForClass

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
): T = getForClass(
    name,
    clazz.canonicalName ?: error("retrieveViewModel can't find class name"),
    module,
    params,
    isViewModel
)

/**
 * Retrieve ViewModel Instance from class
 */
internal fun <T> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    module: String?,
    params: ParameterDefinition
): T = getForClass(
    className = clazz.canonicalName ?: error("retrieveViewModel can't find class name"),
    module = module,
    parameters = params,
    filter = isViewModel
)