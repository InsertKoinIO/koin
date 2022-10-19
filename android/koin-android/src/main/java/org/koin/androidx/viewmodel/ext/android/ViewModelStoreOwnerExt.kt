package org.koin.androidx.viewmodel.ext.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.viewmodel.factory.KoinViewModelFactory
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@KoinInternalApi
@PublishedApi
internal inline fun <reified T : ViewModel> ViewModelStoreOwner.getKoinInstance(
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    noinline parameters: ParametersDefinition? = null,
): T = getViewModelInstance(T::class,this,key, extras, qualifier, scope, parameters)

@KoinInternalApi
@PublishedApi
internal inline fun <T: ViewModel> getViewModelInstance(
    vmClass : KClass<T>,
    owner : ViewModelStoreOwner,
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    noinline parameters: ParametersDefinition? = null,
): T {
    val modelClass = vmClass.java
    val hasSSH = modelClass.constructors[0].parameterTypes.any { p -> p.simpleName == "SavedStateHandle" }
    val factory = KoinViewModelFactory(vmClass, scope, qualifier, parameters, hasSSH)
    val provider = ViewModelProvider(owner.viewModelStore, factory, extras)
    return if (key != null) {
        provider[key, modelClass]
    } else {
        provider[modelClass]
    }
}