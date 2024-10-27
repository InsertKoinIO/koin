/*
 * Copyright 2017-Present the original author or authors.
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

package org.koin.core.instance

import org.koin.core.definition.BeanDefinition
import org.koin.core.error.InstanceCreationException
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatformTools
import org.koin.mp.Lockable

/**
 * Koin Instance Holder
 * create/get/release an instance of given definition
 */
abstract class InstanceFactory<T>(val beanDefinition: BeanDefinition<T>) : Lockable() {

    /**
     * Retrieve an instance
     * @param context
     * @return T
     */
    abstract fun get(context: ResolutionContext): T

    /**
     * Create an instance
     * @param context
     * @return T
     */
    open fun create(context: ResolutionContext): T {
        context.logger.debug("| (+) '$beanDefinition'")
        try {
            val parameters: ParametersHolder = context.parameters ?: emptyParametersHolder()
            return beanDefinition.definition.invoke(
                context.scope,
                parameters,
            )
        } catch (e: Exception) {
            val stack = KoinPlatformTools.getStackTrace(e)
            context.logger.error("* Instance creation error : could not create instance for '$beanDefinition': $stack")
            throw InstanceCreationException("Could not create instance for '$beanDefinition'", e)
        }
    }

    /**
     * Is instance created
     */
    abstract fun isCreated(context: ResolutionContext? = null): Boolean

    /**
     * Drop the instance
     */
    abstract fun drop(scope: Scope? = null)

    abstract fun dropAll()

    companion object {
        const val ERROR_SEPARATOR = "\n\t"
    }
}
