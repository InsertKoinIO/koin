package org.koin.androidx.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.viewmodel.factory.KoinViewModelFactory
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Resolve a ViewModel instance
 *
 * @param vmClass
 * @param viewModelStore
 * @param key
 * @param extras - @see CreationExtras
 * @param qualifier
 * @param scope
 * @param parameters - for instance building injection
 */
@KoinInternalApi
@Deprecated("scope is not used for ViewModel creation")
fun <T : ViewModel> resolveViewModel(
    vmClass: KClass<T>,
    viewModelStore: ViewModelStore,
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    parameters: ParametersDefinition? = null,
): T {
    return resolveViewModel(vmClass, viewModelStore, key, extras, qualifier, scope.getKoin(), parameters)
}

/**
 * Resolve a ViewModel instance
 *
 * @param vmClass
 * @param viewModelStore
 * @param key
 * @param extras - @see CreationExtras
 * @param qualifier
 * @param koin
 * @param parameters - for instance building injection
 */
@KoinInternalApi
fun <T : ViewModel> resolveViewModel(
    vmClass: KClass<T>,
    viewModelStore: ViewModelStore,
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    koin : Koin,
    parameters: ParametersDefinition? = null,
): T {
    val modelClass: Class<T> = vmClass.java
    val factory = KoinViewModelFactory(vmClass, koin.scopeRegistry.rootScope, qualifier, parameters)
    val provider = ViewModelProvider(viewModelStore, factory, extras)
    val vmKey = getViewModelKey(qualifier, key)

    //To help track Keys
//    koin.logger.debug("[vm_key] - provider:$provider - class:$modelClass = $vmKey (q:'${qualifier?.value}', k:'$key')")
    return when {
        vmKey != null -> provider[vmKey, modelClass]
        else -> provider[modelClass]
    }
}

@KoinInternalApi
internal fun getViewModelKey(qualifier: Qualifier?, key: String?): String? {
    return when {
        qualifier != null -> qualifier.value + (key?.let { "_$it" } ?: "")
        key != null -> key
        else -> null
    }
}

/**
 * Resolve a Lazy ViewModel instance
 * used in Main Thread
 *
 * @param vmClass
 * @param viewModelStore
 * @param key
 * @param extras - @see CreationExtras
 * @param qualifier
 * @param scope
 * @param parameters - for instance building injection
 */
@KoinInternalApi
@MainThread
fun <T : ViewModel> lazyResolveViewModel(
    vmClass: KClass<T>,
    viewModelStore: () -> ViewModelStore,
    key: String? = null,
    extras: () -> CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    parameters: (() -> ParametersHolder)? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        resolveViewModel(
            vmClass,
            viewModelStore(),
            key,
            extras(),
            qualifier,
            scope,
            parameters
        )
    }
}