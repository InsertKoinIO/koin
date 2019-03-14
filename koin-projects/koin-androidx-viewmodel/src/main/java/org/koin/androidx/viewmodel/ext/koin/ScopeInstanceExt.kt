package org.koin.androidx.viewmodel.ext.koin

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Lazy getByClass a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> Scope.viewModel(
        lifecycleOwner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
) = lazy { getViewModel<T>(lifecycleOwner, qualifier, parameters) }

/**
 * Get a viewModel instance
 *
 * @param lifecycleOwner
 * @param parameters
 */
inline fun <reified T : ViewModel> Scope.getViewModel(
        lifecycleOwner: LifecycleOwner,
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T = lifecycleOwner.getViewModel(qualifier, this, parameters)