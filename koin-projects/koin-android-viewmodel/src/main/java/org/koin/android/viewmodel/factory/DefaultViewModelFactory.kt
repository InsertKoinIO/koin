package org.koin.android.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.core.scope.Scope

class DefaultViewModelFactory<T : ViewModel>(val scope: Scope,
    val parameters: ViewModelParameter<T>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return scope.get(parameters.clazz, parameters.qualifier, parameters.parameters) as T
    }
}