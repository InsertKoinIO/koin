package org.koin.androidx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ViewModelStoreOwnerAmbient
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelOwner.Companion.from
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.androidx.viewmodel.koin.viewModel
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@KoinExperimentalAPI
@Composable
inline fun <reified T : ViewModel> viewModel(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    val owner = ViewModelStoreOwnerAmbient.current.viewModelStore
    return remember {
        GlobalContext.get().getViewModel(qualifier, owner = { from(owner) }, parameters = parameters)
    }
}

@KoinExperimentalAPI
@Composable
inline fun <reified T : ViewModel> getViewModel(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T {
    val owner = ViewModelStoreOwnerAmbient.current.viewModelStore
    return remember {
        GlobalContext.get().getViewModel(qualifier, owner = { from(owner) }, parameters = parameters)
    }
}
