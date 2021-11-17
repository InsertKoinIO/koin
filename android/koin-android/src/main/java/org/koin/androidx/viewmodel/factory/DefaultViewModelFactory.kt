package org.koin.androidx.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.scope.Scope

class DefaultViewModelFactory<T : ViewModel>(
    val scope: Scope,
    val parameters: ViewModelParameter<T>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return scope.get(parameters.clazz, parameters.qualifier, parameters.parameters) as T
    }
}