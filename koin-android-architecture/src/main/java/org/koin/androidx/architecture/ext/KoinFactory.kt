package org.koin.androidx.architecture.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.parameter.Parameters
import org.koin.standalone.KoinComponent

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
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val beanName = name
        return if (beanName != null) {
            getByName(beanName, parameters)
        } else get(modelClass, parameters)
    }
}