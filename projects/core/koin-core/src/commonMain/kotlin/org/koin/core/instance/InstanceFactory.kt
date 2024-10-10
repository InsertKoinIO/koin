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
@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import co.touchlab.stately.concurrency.AtomicBoolean
import co.touchlab.stately.concurrency.AtomicLong
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
 *
 * Implements [Comparable] to provide factories soring by instance creation order
 */
abstract class InstanceFactory<T>(val beanDefinition: BeanDefinition<T>) : Lockable(), Comparable<InstanceFactory<*>> {
    private val instanceCreationOrderSet = AtomicBoolean(false)
    private var instanceCreationOrderPosition = Long.MAX_VALUE

    /**
     * Retrieve an instance
     * @param context
     * @return T
     */
    abstract fun get(context: InstanceContext): T

    /**
     * Create an instance
     * @param context
     * @return T
     */
    open fun create(context: InstanceContext): T {
        context.logger.debug("| (+) '$beanDefinition'")
        try {
            val parameters: ParametersHolder = context.parameters ?: emptyParametersHolder()
            val instance = beanDefinition.definition.invoke(
                context.scope,
                parameters,
            )
            if (instanceCreationOrderSet.compareAndSet(false, true)) {
                instanceCreationOrderPosition = INSTANCE_CREATION_ORDER_COUNTER.incrementAndGet()

                // just to be sure that counter provides normal values
                check(instanceCreationOrderPosition >= 0) {
                    "Unexpected negative instance creation order position"
                }

                // it's required to have `Long.MAX_VALUE` instantiations happened to make this check fail,
                // which is not likely to happen
                check(instanceCreationOrderPosition < Long.MAX_VALUE) {
                    "Instance creation order position reached Long.MAX_VALUE"
                }
            }
            return instance
        } catch (e: Exception) {
            val stack = KoinPlatformTools.getStackTrace(e)
            context.logger.error("* Instance creation error : could not create instance for '$beanDefinition': $stack")
            throw InstanceCreationException("Could not create instance for '$beanDefinition'", e)
        }
    }

    override fun compareTo(other: InstanceFactory<*>): Int {
        return instanceCreationOrderPosition.compareTo(other.instanceCreationOrderPosition)
    }

    /**
     * Is instance created
     */
    abstract fun isCreated(context: InstanceContext? = null): Boolean

    /**
     * Drop the instance
     */
    abstract fun drop(scope: Scope? = null)

    abstract fun dropAll()

    companion object {
        const val ERROR_SEPARATOR = "\n\t"
        private val INSTANCE_CREATION_ORDER_COUNTER = AtomicLong(-1)
    }
}
