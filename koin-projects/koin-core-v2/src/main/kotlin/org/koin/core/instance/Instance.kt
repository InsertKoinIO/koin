@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

interface Instance<T> {

    fun <T> get(scope: Scope? = null, parameters: ParametersDefinition?): T

    fun <T> create(beanDefinition: BeanDefinition<*>, parameters: ParametersDefinition?): T {
        KoinApplication.log("[Koin] create instance ~ $beanDefinition")
        try {
            val parametersHolder: ParametersHolder = parameters?.let { parameters() } ?: emptyParametersHolder()
            val value = beanDefinition.definition(parametersHolder)
            return value as T
        } catch (e: Exception) {
            //TODO Format error
            e.printStackTrace()
            throw InstanceCreationException("Error while creating instance for $beanDefinition")
        }
    }

    fun isCreated(scope: Scope? = null): Boolean

    fun release(scope: Scope? = null)
}