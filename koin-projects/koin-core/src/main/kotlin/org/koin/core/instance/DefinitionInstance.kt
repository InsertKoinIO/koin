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

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

/**
 * Koin Instance Holder
 * create/get/release an instance of given definition
 */
abstract class DefinitionInstance<T>(val beanDefinition: BeanDefinition<T>) {

    /**
     * Retrieve an instance
     * @param context
     * @return T
     */
    abstract fun <T> get(context: InstanceContext): T

    /**
     * Create an instance
     * @param context
     * @return T
     */
    open fun <T> create(context: InstanceContext): T {
        if (logger.isAt(Level.DEBUG)) {
            logger.debug("| create instance for $beanDefinition")
        }
        try {
            val parameters: DefinitionParameters = context.parameters
            val result = beanDefinition.definition(context.scope ?: error("Can't execute definition instance while this context is not registered against any Koin instance"), parameters)
            return result as T
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
    abstract fun isCreated(context: InstanceContext): Boolean

    /**
     * Release the held instance (if hold)
     */
    abstract fun release(context: InstanceContext)

    /**
     * close the instance allocation from registry
     */
    abstract fun close()

    companion object {
        const val ERROR_SEPARATOR = "\n\t"
    }
}

/**
 * Instance resolution Context
 * Help support DefinitionContext & DefinitionParameters when resolving definition function
 */
class InstanceContext(
    val koin: Koin? = null,
    val scope: Scope? = koin?.rootScope,
    private val _parameters: ParametersDefinition? = null
) {
    val parameters: DefinitionParameters = _parameters?.invoke() ?: emptyParametersHolder()
}
