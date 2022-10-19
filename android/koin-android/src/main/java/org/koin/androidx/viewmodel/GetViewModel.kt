package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.viewmodel.factory.KoinViewModelFactory
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
@KoinInternalApi
inline fun <T : ViewModel> resolveViewModel(
    vmClass: KClass<T>,
    viewModelStore: ViewModelStore,
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    noinline parameters: ParametersDefinition? = null,
): T {
    val modelClass: Class<T> = vmClass.java
    val factory = KoinViewModelFactory(vmClass, scope, qualifier, parameters, modelClass.needSavedStateHandle())
    val provider = ViewModelProvider(viewModelStore, factory, extras)
    return if (key != null) {
        provider[key, modelClass]
    } else {
        provider[modelClass]
    }
}

@KoinInternalApi
@PublishedApi
internal fun <T : ViewModel> Class<T>.needSavedStateHandle(): Boolean {
    return constructors[0].parameterTypes.any { p -> p.simpleName == "SavedStateHandle" }
}