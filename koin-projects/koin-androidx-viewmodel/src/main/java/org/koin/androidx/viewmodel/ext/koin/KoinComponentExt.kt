package org.koin.androidx.viewmodel.ext.koin

import androidx.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.core.parameter.ParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

/**
 * Create instance with parameters for ViewModelFactory
 * @see implemented ViewModelFactory
 *
 * @param p - ViewModelParameters
 * @param clazz - class
 */
fun <T : ViewModel> KoinComponent.createInstance(p: ViewModelParameters, clazz: Class<T>): T {
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
internal fun <T : ViewModel> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    name: String,
    module: String?,
    params: ParameterDefinition
): T = get(name, clazz.kotlin, module, params, isViewModel)

/**
 * Retrieve ViewModel Instance from class
 */
internal fun <T : ViewModel> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    module: String?,
    params: ParameterDefinition
): T = get(clazz = clazz.kotlin, module = module, parameters = params, filter = isViewModel)