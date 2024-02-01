@file:Suppress("DEPRECATION")

package org.koin.androidx.viewmodel.ext.android

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.resolveViewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
@MainThread
fun <T : ViewModel> ComponentActivity.viewModelForClass(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = this,
    state: BundleDefinition? = null,
    key: String? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    val viewModelStore = owner.viewModelStore
    return lazy(LazyThreadSafetyMode.NONE) {
        resolveViewModel(
            clazz,
            viewModelStore,
            extras = state?.invoke()?.toExtras(owner) ?: CreationExtras.Empty,
            qualifier = qualifier,
            parameters = parameters,
            key = key,
            scope = getKoinScope()
        )
    }
}

@OptIn(KoinInternalApi::class)
@MainThread
fun <T : ViewModel> Fragment.viewModelForClass(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    owner: () -> ViewModelStoreOwner = { this },
    state: BundleDefinition? = null,
    key: String? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        val ownerEager = owner()
        val viewModelStore = ownerEager.viewModelStore
        resolveViewModel(
            clazz,
            viewModelStore,
            extras = state?.invoke()?.toExtras(ownerEager) ?: CreationExtras.Empty,
            qualifier = qualifier,
            parameters = parameters,
            key = key,
            scope = getKoinScope()
        )
    }
}

@OptIn(KoinInternalApi::class)
@MainThread
fun <T : ViewModel> getLazyViewModelForClass(
    clazz: KClass<T>,
    owner: ViewModelStoreOwner,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    qualifier: Qualifier? = null,
    state: BundleDefinition? = null,
    key: String? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    val viewModelStore = owner.viewModelStore
    return lazy(LazyThreadSafetyMode.NONE) {
        resolveViewModel(
            clazz,
            viewModelStore,
            extras = state?.invoke()?.toExtras(owner) ?: CreationExtras.Empty,
            qualifier = qualifier,
            parameters = parameters,
            key = key,
            scope = scope
        )
    }
}

@OptIn(KoinInternalApi::class)
@MainThread
inline fun <reified T : ViewModel> lazyViewModelForClass(
    crossinline viewModelStoreOwnerLazy: () -> ViewModelStoreOwner,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition? = null,
    key: String? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    val viewModelStoreOwner = viewModelStoreOwnerLazy()
    resolveViewModel(
        vmClass = T::class,
        viewModelStore = viewModelStoreOwner.viewModelStore,
        extras = state?.invoke()?.toExtras(viewModelStoreOwner) ?: CreationExtras.Empty,
        qualifier = qualifier,
        parameters = parameters,
        key = key,
        scope = scope
    )
}