package org.koin.android.viewmodel.koin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelStoreOwner
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.android.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel<T>(owner, qualifier, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(owner, T::class, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
    owner: ViewModelStoreOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getViewModel(owner, clazz, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return _scopeRegistry.rootScope.getViewModel(viewModelParameters)
}