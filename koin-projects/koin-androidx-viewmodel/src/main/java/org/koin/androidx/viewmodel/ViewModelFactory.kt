package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ext.koin.get
import org.koin.androidx.viewmodel.ext.koin.getByName
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 */
object ViewModelFactory : ViewModelProvider.Factory, KoinComponent {

    /**
     * Current Parameters
     */
    internal var _parameters: ParameterDefinition = emptyParameterDefinition()

    /**
     * Current BeanDefinition name
     */
    internal var _name: String? = null

    /**
     * Module Path
     */
    internal var _module: String? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val beanName = _name
        return if (beanName != null) {
            getByName(beanName, _module, _parameters)
        } else get(modelClass, _module, _parameters)
    }
}