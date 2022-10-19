package org.koin.androidx.viewmodel.ext.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.pickFactory
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@KoinInternalApi
@Deprecated("")
inline fun <reified T : ViewModel> getViewModelFactory(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier?,
    noinline parameters: ParametersDefinition?,
    noinline state: BundleDefinition? = null,
    scope: Scope
): ViewModelProvider.Factory {
    return getViewModelFactory(owner, T::class, qualifier, parameters, state, scope)
}

@KoinInternalApi
@Deprecated("")
fun <T : ViewModel> Scope.getViewModelFactory(
    parameters: ViewModelParameter<T>
): ViewModelProvider.Factory {
    return pickFactory(parameters)
}

@KoinInternalApi
@Deprecated("")
fun <T : ViewModel> getViewModelFactory(
    owner: ViewModelStoreOwner,
    clazz: KClass<T>,
    qualifier: Qualifier?,
    parameters: ParametersDefinition?,
    state: BundleDefinition? = null,
    scope: Scope
): ViewModelProvider.Factory {
    val hasState = (state != null)
    val viewModelParameters = ViewModelParameter(
        clazz = clazz,
        qualifier = qualifier,
        parameters = parameters,
        state = state,
        viewModelStoreOwner = owner,
        registryOwner = if (hasState) owner as? SavedStateRegistryOwner else null
    )
    return scope.pickFactory(viewModelParameters)
}