package org.koin.android.viewmodel.koin

import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ViewModelOwnerDefinition
import org.koin.android.viewmodel.ViewModelParameter
import org.koin.android.viewmodel.scope.getViewModel
import org.koin.core.Koin
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
        qualifier: Qualifier? = null,
        noinline owner: ViewModelOwnerDefinition,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { getViewModel<T>(qualifier, owner, parameters) }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
        qualifier: Qualifier? = null,
        noinline owner: ViewModelOwnerDefinition,
        noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(qualifier, owner, T::class, parameters)
}

@OptIn(KoinApiExtension::class)
fun <T : ViewModel> Koin.getViewModel(
        qualifier: Qualifier? = null,
        owner: ViewModelOwnerDefinition,
        clazz: KClass<T>,
        parameters: ParametersDefinition? = null
): T {
    return onScopeRegistry { rootScope.getViewModel(qualifier, owner, clazz, parameters) }
}

@OptIn(KoinApiExtension::class)
fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return onScopeRegistry { rootScope.getViewModel(viewModelParameters) }
}