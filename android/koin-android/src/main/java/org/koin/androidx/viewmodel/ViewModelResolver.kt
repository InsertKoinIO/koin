package org.koin.androidx.viewmodel

import androidx.lifecycle.StateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return if (viewModelParameters.qualifier != null) {
        get(viewModelParameters.qualifier.toString(), javaClass)
    } else {
        get(javaClass)
    }
}

internal fun <T : ViewModel> Scope.pickFactory(
    viewModelParameters: ViewModelParameter<T>,
): ViewModelProvider.Factory {
    //TODO Move out initialState (as it's already injected by bundle) - can be a boolean
    val injectHandle : Boolean = viewModelParameters.registryOwner != null && viewModelParameters.initialState != null
    return StateViewModelFactory(this, viewModelParameters, injectHandle = injectHandle)
}
