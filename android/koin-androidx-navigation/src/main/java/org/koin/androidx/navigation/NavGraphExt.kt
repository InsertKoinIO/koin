package org.koin.androidx.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.KoinViewModelLazy
import org.koin.androidx.viewmodel.ext.android.getViewModelFactory
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition

/**
 * Request a ViewModel instance, scoped to Navigation graph
 *
 * @param navGraphId
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
inline fun <reified VM : ViewModel> Fragment.koinNavGraphViewModel(
    @IdRes navGraphId: Int,
    key: String? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }
    val scope = getKoinScope()
    return KoinViewModelLazy(VM::class, key, { backStackEntry.viewModelStore }) {
        getViewModelFactory<VM>(
            owner = backStackEntry,
            qualifier = null,
            parameters = parameters,
            scope = scope
        )
    }
}
