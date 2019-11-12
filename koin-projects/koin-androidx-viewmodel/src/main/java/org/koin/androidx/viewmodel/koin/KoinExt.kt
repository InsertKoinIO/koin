package org.koin.androidx.viewmodel.koin

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.Koin

/**
 * resolve instance
 * @param viewModelParameters
 */
fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return rootScope.getViewModel(viewModelParameters)
}