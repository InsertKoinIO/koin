package org.koin.androidx.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.getKoinScope
import org.koin.androidx.viewmodel.ViewModelOwner
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
    noinline parameters: ParametersDefinition? = null
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }
    val scope = getKoinScope()
    return viewModels(ownerProducer = {backStackEntry}) {
        getViewModelFactory<VM>(owner = { ViewModelOwner(backStackEntry) }, qualifier = null, parameters =  parameters, scope = scope)
    }
}
