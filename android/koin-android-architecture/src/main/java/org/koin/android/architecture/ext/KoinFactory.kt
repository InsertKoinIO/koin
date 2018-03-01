package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.ParameterMap
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {

    /**
     * Parameters
     */
    internal var parameters: ParameterMap = emptyMap()

    /**
     * Bean name
     */
    internal var name: String? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val beanName = name
        return if (beanName != null) {
            getByName(beanName, parameters)
        } else get(modelClass, parameters)
    }
}