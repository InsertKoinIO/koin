package org.koin.androidx.viewmodel.koin

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.SavedStateRegistryOwnerDefinition
import org.koin.androidx.viewmodel.scope.ViewModelStoreDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
    qualifier: Qualifier? = null,
    noinline store: ViewModelStoreDefinition,
    noinline stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel<T>(qualifier, store, stateRegistry, state, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
    qualifier: Qualifier? = null,
    noinline store: ViewModelStoreDefinition,
    noinline stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(T::class, qualifier, store, stateRegistry, state, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    store: ViewModelStoreDefinition,
    stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getViewModel(clazz, store, stateRegistry, qualifier, state, parameters)
}

fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return _scopeRegistry.rootScope.getViewModel(viewModelParameters)
}