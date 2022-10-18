package org.koin.androidx.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ext.android.getViewModelFactory
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

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
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }
    val scope: Scope by lazy { getKoinScope() }
    return viewModels(ownerProducer = { backStackEntry }) {
        getViewModelFactory<VM>(
            owner = backStackEntry,
            qualifier = qualifier,
            parameters = parameters,
            scope = scope
        )
    }
}
