package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

/**
 * Create Bundle/State ViewModel Factory
 */
fun <T : ViewModel> Scope.stateViewModelFactory(
    viewModelParameters: ViewModelParameter<T>
): AbstractSavedStateViewModelFactory {
    return object : AbstractSavedStateViewModelFactory(
        viewModelParameters.owner as? SavedStateRegistryOwner ?: error("Owner '${viewModelParameters.owner}' is not SavedStateRegistryOwner"),
        viewModelParameters.state?.invoke() ?: Bundle()
    ) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return get(viewModelParameters.clazz, viewModelParameters.qualifier) {
                parametersOf(
                    handle,
                    *(viewModelParameters.parameters?.invoke() ?: emptyParametersHolder()).values
                )
            }
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