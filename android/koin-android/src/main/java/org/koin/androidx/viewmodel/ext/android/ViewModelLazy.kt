package org.koin.androidx.viewmodel.ext.android

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
@Deprecated("Deprecated in favor of getViewModel() function")
fun <T : ViewModel> ComponentActivity.viewModelForClass(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = this,
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    return ViewModelLazy(
            viewModelClass = clazz,
            storeProducer = { owner.viewModelStore },
            factoryProducer = { getViewModelFactory(owner, clazz, qualifier, parameters, state, getKoinScope()) }
        )
}

@OptIn(KoinInternalApi::class)
@Deprecated("Deprecated in favor of getViewModel() function")
fun <T : ViewModel> Fragment.viewModelForClass(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = this,
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    return ViewModelLazy(
        viewModelClass = clazz,
        storeProducer = { owner.viewModelStore },
        factoryProducer = { getViewModelFactory(owner, clazz, qualifier, parameters, state, getKoinScope()) }
    )
}

@OptIn(KoinInternalApi::class)
@Deprecated("Deprecated in favor of getViewModel() function")
fun <T : ViewModel> getLazyViewModelForClass(
    clazz: KClass<T>,
    owner: ViewModelStoreOwner,
    scope: Scope,
    qualifier: Qualifier? = null,
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T> {
    return ViewModelLazy(
        viewModelClass = clazz,
        storeProducer = { owner.viewModelStore },
        factoryProducer = { getViewModelFactory(owner, clazz, qualifier, parameters, state, scope) }
    )
}