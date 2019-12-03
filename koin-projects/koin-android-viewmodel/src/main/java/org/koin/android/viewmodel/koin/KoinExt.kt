package org.koin.android.viewmodel.koin

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.android.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
        owner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { _scopeRegistry.rootScope.getViewModel<T>(owner, qualifier, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
        owner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getViewModel(owner, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
        owner: LifecycleOwner,
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getViewModel(owner, clazz, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return _scopeRegistry.rootScope.getViewModel(viewModelParameters)
}