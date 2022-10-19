package org.koin.android.compat

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
fun <T : ViewModel> resolveViewModelCompat(
    vmClass: Class<T>,
    viewModelStore: ViewModelStore,
    key: String? = null,
    extras: CreationExtras,
    qualifier: Qualifier? = null,
    scope: Scope,
    parameters: ParametersDefinition? = null,
): T = org.koin.androidx.viewmodel.resolveViewModel(vmClass.kotlin, viewModelStore, key, extras, qualifier, scope, parameters)