package org.koin.androidx.viewmodel.koin

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.scope.getStateViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.stateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getStateViewModel<T>(owner, qualifier, bundle, parameters)
    }
}

inline fun <reified T : ViewModel> Koin.getStateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getStateViewModel(owner, T::class, qualifier, bundle, parameters)
}

fun <T : ViewModel> Koin.getStateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getStateViewModel(owner, clazz, qualifier, bundle, parameters)
}