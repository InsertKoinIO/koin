package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.factory.DefaultViewModelFactory
import org.koin.androidx.viewmodel.factory.StateViewModelFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return get(viewModelParameters, viewModelParameters.qualifier, javaClass)
}

internal fun <T : ViewModel> ViewModelProvider.get(
        viewModelParameters: ViewModelParameter<T>,
        qualifier: Qualifier?,
        javaClass: Class<T>,
): T {
    return if (viewModelParameters.qualifier != null) {
        get(qualifier.toString(), javaClass)
    } else {
        get(javaClass)
    }
}

internal fun <T : ViewModel> Scope.createViewModelProvider(
        viewModelParameters: ViewModelParameter<T>,
): ViewModelProvider {
    return ViewModelProvider(
            viewModelParameters.viewModelStore, pickFactory(viewModelParameters)
    )
}

private fun <T : ViewModel> Scope.pickFactory(
        viewModelParameters: ViewModelParameter<T>,
): ViewModelProvider.Factory {
    return when {
        viewModelParameters.registryOwner != null && viewModelParameters.initialState != null -> StateViewModelFactory(this, viewModelParameters)
        else -> DefaultViewModelFactory(this, viewModelParameters)
    }
}
