package org.koin.androidx.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

/**
 * Create Bundle/State ViewModel Factory
 */
fun <T : ViewModel> Scope.stateViewModelFactory(
    vmParams: ViewModelParameter<T>,
    stateBundle: StateBundle
): AbstractSavedStateViewModelFactory {
    val registryOwner = (vmParams.stateRegistryOwner
        ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"))
    return object : AbstractSavedStateViewModelFactory(registryOwner, stateBundle.defaultState) {
        override fun <T : ViewModel?> create(
            key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            return get(
                vmParams.clazz,
                vmParams.qualifier
            ) { parametersOf(*updateParameters(handle)) }
        }

        private fun updateParameters(handle: SavedStateHandle): Array<out Any?> {
            val values = stateBundle.currentValues.asList() as MutableList<Any?>
            values[stateBundle.index] = handle
            return values.toTypedArray()
        }
    }
}

/**
 * Create Default ViewModel Factory
 */
fun <T : ViewModel> Scope.defaultViewModelFactory(parameters: ViewModelParameter<T>): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return get(parameters.clazz, parameters.qualifier, parameters.parameters)
        }
    }
}