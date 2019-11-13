package org.koin.android.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.core.scope.Scope

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