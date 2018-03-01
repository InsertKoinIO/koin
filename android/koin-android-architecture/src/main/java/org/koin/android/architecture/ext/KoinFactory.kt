package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.ParameterMap
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {

    internal var parameters: ParameterMap = emptyMap()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass, parameters)
    }
}