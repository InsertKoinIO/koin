/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.ScopeInstance

/**
 * Koin Instance Holder
 * create/get/release an instance of given definition
 */
abstract class Instance<T>(val beanDefinition: BeanDefinition<T>) {

    /**
     * Retrieve an instance
     * @param scope
     * @param parameters
     * @return T
     */
    abstract fun <T> get(scope: ScopeInstance? = null, parameters: ParametersDefinition?): T

    /**
     * Create an instance
     * @param beanDefinition
     * @param parameters
     * @return T
     */
    open fun <T> create(beanDefinition: BeanDefinition<*>, parameters: ParametersDefinition?): T {
        if (logger.level == Level.DEBUG) {
            logger.debug("| create instance for $beanDefinition")
        }
        try {
            val parametersHolder: ParametersHolder = parameters?.let { parameters() } ?: emptyParametersHolder()
            val value = beanDefinition.definition(parametersHolder)
            return value as T
        } catch (e: Exception) {
            val stack =
                e.toString() + ERROR_SEPARATOR + e.stackTrace.takeWhile { !it.className.contains("sun.reflect") }
                    .joinToString(ERROR_SEPARATOR)
            logger.error("Instance creation error : could not create instance for $beanDefinition: $stack")
            throw InstanceCreationException("Could not create instance for $beanDefinition", e)
        }
    }

    /**
     * Is instance created
     */
    abstract fun isCreated(scope: ScopeInstance? = null): Boolean

    /**
     * Release the held instance (if hold)
     */
    abstract fun release(scope: ScopeInstance? = null)

    companion object {
        const val ERROR_SEPARATOR = "\n\t"
    }
}