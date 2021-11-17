package org.koin.androidx.viewmodel

import androidx.lifecycle.StateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.factory.DefaultViewModelFactory
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
    return if (viewModelParameters.registryOwner != null) StateViewModelFactory(this, viewModelParameters)
            else DefaultViewModelFactory(this,viewModelParameters)
}
