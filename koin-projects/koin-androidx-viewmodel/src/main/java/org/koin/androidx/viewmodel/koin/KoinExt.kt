package org.koin.androidx.viewmodel.koin

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
    owner: LifecycleOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { rootScope.getViewModel<T>(owner, qualifier, parameters) }
}

inline fun <reified T : ViewModel> Koin.viewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { rootScope.getViewModel<T>(owner, qualifier, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
    owner: LifecycleOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return rootScope.getViewModel(owner, qualifier, parameters)
}

inline fun <reified T : ViewModel> Koin.getViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return rootScope.getViewModel(owner, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
    owner: LifecycleOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return rootScope.getViewModel(owner, clazz, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return rootScope.getViewModel(owner, clazz, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return rootScope.getViewModel(viewModelParameters)
}