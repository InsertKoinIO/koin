@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

abstract class Instance {

    abstract fun <T> get(scope: Scope? = null, parameters: ParametersDefinition?): T

    fun <T> create(beanDefinition: BeanDefinition<*>, parameters: ParametersDefinition?): T {
        logger.debug("| create instance")
        try {
            val parametersHolder: ParametersHolder = parameters?.let { parameters() } ?: emptyParametersHolder()
            val value = beanDefinition.definition(parametersHolder)
            return value as T
        } catch (e: Exception) {
            val stack =
                e.toString() + ERROR_SEPARATOR + e.stackTrace.takeWhile { !it.className.contains("sun.reflect") }
                    .joinToString(ERROR_SEPARATOR)
            logger.error("Could not create instance for $beanDefinition: \n$stack")
            throw InstanceCreationException("Could not create instance for $beanDefinition", e)
        }
    }


    abstract fun isCreated(scope: Scope? = null): Boolean

    abstract fun release(scope: Scope? = null)

    companion object {
        const val ERROR_SEPARATOR = "\n\t"
    }
}