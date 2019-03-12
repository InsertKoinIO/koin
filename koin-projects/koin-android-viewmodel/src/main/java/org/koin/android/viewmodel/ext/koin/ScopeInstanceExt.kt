package org.koin.android.viewmodel.ext.koin

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
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
): Lazy<T> = lazy { getViewModel<T>(lifecycleOwner, qualifier, parameters) }

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
): T = lifecycleOwner.getViewModel<T>(qualifier, this, parameters)