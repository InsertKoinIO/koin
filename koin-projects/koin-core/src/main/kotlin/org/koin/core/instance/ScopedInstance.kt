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
package org.koin.core.instance

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.Level
import org.koin.core.scope.ScopeInstance
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope definition Instance holder
 * @author Arnaud Giuliani
 */
class ScopedInstance<T>(beanDefinition: BeanDefinition<T>) : Instance<T>(beanDefinition) {


    private val values: MutableMap<String, T> = ConcurrentHashMap()

    override fun isCreated(context: InstanceContext): Boolean = context.scope.let { values[context.scope.id] != null }

    override fun release(context: InstanceContext) {
        val scope = context.scope
        if (logger.level == Level.DEBUG) {
            logger.debug("releasing '$scope' ~ $beanDefinition ")
        }
        beanDefinition.onRelease?.invoke(values[scope.id])
        values.remove(scope.id)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(context: InstanceContext): T {
        if (context.scope == ScopeInstance.GLOBAL) {
            throw ScopeNotCreatedException("No scope instance created to resolve $beanDefinition")
        }
        val scope = context.scope
        val internalId = scope.id
        var current = values[internalId]
        if (current == null) {
            current = create(context)
            values[internalId] = current
                    ?: error("Instance creation from $beanDefinition should not be null")
        }
        return current as T
    }

    override fun close() {
        beanDefinition.onClose?.invoke(null)
        values.clear()
    }
}