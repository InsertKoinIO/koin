package org.koin.android.architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.android.architecture.ext.koin.get
import org.koin.android.architecture.ext.koin.getByName
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {

    /**
     * Current Parameters
     */
    internal var parameters: ParameterDefinition = emptyParameterDefinition()

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