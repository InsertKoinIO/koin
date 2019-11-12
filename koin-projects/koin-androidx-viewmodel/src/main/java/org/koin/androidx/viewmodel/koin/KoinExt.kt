package org.koin.androidx.viewmodel.koin

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.Koin

/**
 * resolve instance
 * @param viewModelParameters
 */
fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameters<T>): T {
    return rootScope.getViewModel(viewModelParameters)
}