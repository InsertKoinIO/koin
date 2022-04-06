package org.koin.androidx.viewmodel.ext.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.pickFactory
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@KoinInternalApi
inline fun <reified T : ViewModel> getViewModelFactory(
    owner: ViewModelOwnerDefinition,
    qualifier: Qualifier?,
    noinline parameters: ParametersDefinition?,
    noinline state: BundleDefinition? = null,
    scope: Scope
): ViewModelProvider.Factory {
    val ownerValue = owner()
    val viewModelParameters = ViewModelParameter(
        clazz = T::class,
        qualifier = qualifier,
        parameters = parameters,
        state = state,
        viewModelStoreOwner = ownerValue.storeOwner,
        registryOwner = ownerValue.stateRegistry
    )
    return scope.pickFactory(viewModelParameters)
}

@KoinInternalApi
fun <T : ViewModel> getViewModelFactory(
    owner: ViewModelOwnerDefinition,
    clazz: KClass<T>,
    qualifier: Qualifier?,
    parameters: ParametersDefinition?,
    state: BundleDefinition? = null,
    scope: Scope
): ViewModelProvider.Factory {
    val ownerValue = owner()
    val viewModelParameters = ViewModelParameter(
        clazz = clazz,
        qualifier = qualifier,
        parameters = parameters,
        state = state,
        viewModelStoreOwner = ownerValue.storeOwner,
        registryOwner = ownerValue.stateRegistry
    )
    return scope.pickFactory(viewModelParameters)
}