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
package org.koin.test.check

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.Instance
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.test.error.BrokenDefinitionException
import org.mockito.Mockito.mock

/**
 * Sandbox Instance Holder - let execute the definition but return a mock of it
 *
 * @author Arnaud Giuliani
 */
@Suppress("UNCHECKED_CAST")
class SandboxInstance<T>(beanDefinition: BeanDefinition<T>) : Instance<T>(beanDefinition) {

    private var value: T? = null

    override fun <T> get(context: InstanceContext): T {
        if (value == null) {
            value = create(context)
        }
        return value as? T ?: error("SandboxInstance should return a value for $beanDefinition")
    }

    override fun <T> create(context: InstanceContext): T {
        try {
            val params = context.getParameters()
            val instanceContext = context.getDefinitionContext()
            beanDefinition.definition(instanceContext, params)
        } catch (e: Exception) {
            when (e) {
                is NoBeanDefFoundException, is InstanceCreationException, is DefinitionOverrideException -> {
                    throw BrokenDefinitionException("Definition $beanDefinition is broken due to error : $e")
                }
                else -> logger.debug("sandbox resolution continue on caught error: $e")
            }
        }
        if (logger.isAt(Level.DEBUG)) {
            logger.debug("| create sandbox for $beanDefinition")
        }
        return mock(beanDefinition.primaryType.java) as T
    }

    override fun isCreated(context: InstanceContext): Boolean = (value == null)

    override fun release(context: InstanceContext) {}
}