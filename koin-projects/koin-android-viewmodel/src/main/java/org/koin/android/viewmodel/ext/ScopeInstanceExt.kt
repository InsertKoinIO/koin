package org.koin.android.viewmodel.ext

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

/**
 * Lazy getByClass a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> ScopeInstance.viewModel(
        lifecycleOwner: LifecycleOwner,
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
) = lazy { getViewModel<T>(lifecycleOwner, name, parameters) }

/**
 * Get a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> ScopeInstance.getViewModel(
        lifecycleOwner: LifecycleOwner,
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
) = lifecycleOwner.getViewModel<T>(name, this, parameters)