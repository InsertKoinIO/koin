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
import org.koin.core.bean.BeanDefinition
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope definition Instance holder
 * @author Arnaud Giuliani
 */
class ScopeInstance<T>(beanDefinition: BeanDefinition<T>) : Instance<T>(beanDefinition) {

    override fun isCreated(scope: Scope?): Boolean = scope?.let { values[scope.internalId] != null } ?: false

    private val values: MutableMap<String, T> = ConcurrentHashMap()

    override fun release(scope: Scope?) {
        scope?.let {
            if (logger.level == Level.DEBUG) {
                logger.debug("releasing '$scope' ~ $beanDefinition ")
            }
            values.remove(scope.internalId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(scope: Scope?, parameters: ParametersDefinition?): T {
        if (scope == null) error("Scope should not be null for ScopeInstance")

        val internalId = scope.internalId
        var current = values[internalId]
        if (current == null) {
            current = create(beanDefinition, parameters)
            values[internalId] = current ?: error("Instance creation from $beanDefinition should not be null")
        }
        return current as T
    }
}