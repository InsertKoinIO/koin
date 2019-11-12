package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModelStore
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameters<T : Any>(
    val clazz: KClass<T>,
    val store: ViewModelStore,
    val qualifier: Qualifier? = null,
    val parameters: ParametersDefinition? = null,
    val defaultArguments: ViewModelState? = null
)

typealias ViewModelStoreDefinition = () -> ViewModelStore
typealias ViewModelState = () -> Bundle

inline fun <reified T : Any> ViewModelStore.createViewModelParameters(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
    noinline defaultArguments: ViewModelState? = null
): ViewModelParameters<T> =
    ViewModelParameters(T::class, this, qualifier, parameters, defaultArguments)