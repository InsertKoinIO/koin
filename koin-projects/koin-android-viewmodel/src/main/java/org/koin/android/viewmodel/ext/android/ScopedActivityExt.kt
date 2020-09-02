package org.koin.android.viewmodel.ext.android

import android.arch.lifecycle.ViewModel
import org.koin.android.scope.ScopeActivity
import org.koin.android.viewmodel.ViewModelOwner
import org.koin.android.viewmodel.ViewModelOwnerDefinition
import org.koin.android.viewmodel.scope.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * ComponentActivity extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : ViewModel> ScopeActivity.viewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this) },
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel<T>(qualifier, owner, parameters)
    }
}

fun <T : ViewModel> ScopeActivity.viewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this) },
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel(qualifier, owner, clazz, parameters) }
}

inline fun <reified T : ViewModel> ScopeActivity.getViewModel(
    qualifier: Qualifier? = null,
    noinline owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this) },
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(qualifier, owner, T::class, parameters)
}

fun <T : ViewModel> ScopeActivity.getViewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this) },
    clazz: KClass<T>,
    parameters: ParametersDefinition? = null
): T {
    return scope.getViewModel(qualifier, owner, clazz, parameters)
}