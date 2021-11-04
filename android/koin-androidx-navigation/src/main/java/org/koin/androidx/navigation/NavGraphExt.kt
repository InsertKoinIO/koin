package org.koin.androidx.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope

/**
 * Request a ViewModel instance, scoped to Navigation graph
 *
 * @param navGraphId
 *
 * @author Arnaud Giuliani
 */
inline fun <reified VM : ViewModel> Fragment.koinNavGraphViewModel(
    @IdRes navGraphId: Int,
    noinline parameters: ParametersDefinition? = null
): Lazy<VM> {
    val backStackEntry: NavBackStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }
    return lazy(LazyThreadSafetyMode.NONE) {
        getKoinScope(this).getViewModel(
            owner = { ViewModelOwner(backStackEntry.viewModelStore) }, parameters = parameters
        )
    }
}

@OptIn(KoinInternalApi::class)
@PublishedApi
internal fun getKoinScope(any: Any): Scope {
    return when (any) {
        is KoinComponent -> any.getKoin().scopeRegistry.rootScope
        else -> GlobalContext.get().scopeRegistry.rootScope
    }
}