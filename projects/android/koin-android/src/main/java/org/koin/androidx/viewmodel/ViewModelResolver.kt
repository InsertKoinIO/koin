package org.koin.androidx.viewmodel

import androidx.lifecycle.StateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.factory.DefaultViewModelFactory
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope


@Deprecated("Deprecated API in favor of KoinViewModelFactory")
@KoinInternalApi
internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return if (viewModelParameters.qualifier != null) {
        get(viewModelParameters.qualifier.toString(), javaClass)
    } else {
        get(javaClass)
    }
}

@Deprecated("Deprecated API in favor of KoinViewModelFactory")
@KoinInternalApi
fun <T : ViewModel> Scope.pickFactory(
    viewModelParameters: ViewModelParameter<T>,
): ViewModelProvider.Factory {
    return if (viewModelParameters.registryOwner != null && viewModelParameters.state != null) StateViewModelFactory(this, viewModelParameters)
            else DefaultViewModelFactory(this,viewModelParameters)
}
