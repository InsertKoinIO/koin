package org.koin.android.architecture

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.android.architecture.ext.koin.get
import org.koin.android.architecture.ext.koin.getByName
import org.koin.core.parameter.Parameters
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {

    /**
     * Current Parameters
     */
    internal var parameters: Parameters = { emptyMap() }

    /**
     * Current BeanDefinition name
     */
    internal var name: String? = null

    /**
     * Module Path
     */
    internal var module: String? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val beanName = name
        return if (beanName != null) {
            getByName(beanName, module, parameters)
        } else get(modelClass, module, parameters)
    }
}