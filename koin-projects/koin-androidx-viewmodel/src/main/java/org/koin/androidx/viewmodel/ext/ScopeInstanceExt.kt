package org.koin.androidx.viewmodel.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.resolveViewModelInstance
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

/**
 * Resolve ViewModel instance against current ScopeInstance
 * @param lifecycleOwner
 * @param name
 * @param parameters - ParametersDefinition
 */
inline fun <reified T : ViewModel> ScopeInstance.viewMode(
        lifecycleOwner: LifecycleOwner,
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
) = lazy {
    getViewMode<T>(lifecycleOwner, name, parameters)
}

/**
 * Resolve ViewModel instance against current ScopeInstance
 * @param lifecycleOwner
 * @param name
 * @param parameters - ParametersDefinition
 */
inline fun <reified T : ViewModel> ScopeInstance.getViewMode(
        lifecycleOwner: LifecycleOwner,
        name: String? = null,
        noinline parameters: ParametersDefinition? = null
): T {
    return lifecycleOwner.resolveViewModelInstance(
            ViewModelParameters(
                    T::class,
                    name,
                    scope = this,
                    parameters = parameters
            )
    )
}