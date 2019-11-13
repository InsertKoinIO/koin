package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.scope.Scope

/**
 * Create Bundle/State ViewModel Factory
 */
fun <T : ViewModel> Scope.stateViewModelFactory(
    vmParams: ViewModelParameter<T>,
    stateBundle: Bundle
): AbstractSavedStateViewModelFactory {
    val registryOwner = (vmParams.stateRegistryOwner
        ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"))
    return object : AbstractSavedStateViewModelFactory(registryOwner, stateBundle) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            return get(
                vmParams.clazz, vmParams.qualifier, vmParams.parameters
            )
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