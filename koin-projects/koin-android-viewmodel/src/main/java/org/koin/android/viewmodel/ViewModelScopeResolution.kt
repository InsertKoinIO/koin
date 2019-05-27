package org.koin.android.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStore
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