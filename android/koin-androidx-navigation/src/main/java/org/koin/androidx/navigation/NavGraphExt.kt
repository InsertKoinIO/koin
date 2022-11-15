package org.koin.androidx.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier

/**
 * Request a ViewModel instance, scoped to Navigation graph
 *
 * @param navGraphId
 *
 * @author Arnaud Giuliani
 */
inline fun <reified VM : ViewModel> Fragment.koinNavGraphViewModel(
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = { findNavController().getBackStackEntry(navGraphId) },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<VM> {
    return viewModel(qualifier, ownerProducer, extrasProducer, parameters)
}

/**
 * Request a ViewModel instance, scoped to Navigation graph route
 *
 * @param route
 *
 * @author Marco Cattaneo
 */
inline fun <reified VM : ViewModel> Fragment.koinNavGraphViewModel(
    route: String,
    qualifier: Qualifier? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = { findNavController().getBackStackEntry(route) },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<VM> {
    return viewModel(qualifier, ownerProducer, extrasProducer, parameters)
}