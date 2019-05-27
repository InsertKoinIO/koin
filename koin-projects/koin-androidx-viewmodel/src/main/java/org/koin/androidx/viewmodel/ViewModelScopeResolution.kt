package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import org.koin.core.scope.Scope

/**
 * resolve instance
 * @param parameters
 */
fun <T : ViewModel> Scope.getViewModel(parameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = parameters.owner.getViewModelStore(parameters)
    val viewModelProvider = createViewModelProvider(vmStore, parameters)
    return viewModelProvider.getInstance(parameters)
}