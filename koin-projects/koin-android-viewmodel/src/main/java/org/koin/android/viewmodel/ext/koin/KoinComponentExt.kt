package org.koin.android.viewmodel.ext.koin

import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.scope.Scope
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
    val scope = p.scope
    val params = p.parameters
    // Clear local stuff

    return if (name != null) {
        retrieveViewModel(clazz, name, scope, params)
    } else retrieveViewModel(clazz, scope, params)
}

/**
 * Retrieve ViewModel Instance from class/name
 */
internal fun <T : ViewModel> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    name: String,
    scope: Scope? = null,
    params: ParameterDefinition
): T = get(name, clazz.kotlin, scope, params, isViewModel)

/**
 * Retrieve ViewModel Instance from class
 */
internal fun <T : ViewModel> KoinComponent.retrieveViewModel(
    clazz: Class<T>,
    scope: Scope? = null,
    params: ParameterDefinition
): T = get(clazz = clazz.kotlin, scope = scope, parameters = params, filter = isViewModel)