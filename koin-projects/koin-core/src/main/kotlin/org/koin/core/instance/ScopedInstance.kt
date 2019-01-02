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

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.Level
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope definition DefaultInstance holder
 * @author Arnaud Giuliani
 */
class ScopedInstance<T>(koin: Koin, beanDefinition: BeanDefinition<T>) : DefaultInstance<T>(koin, beanDefinition) {

    override fun isCreated(scope: ScopeInstance?): Boolean = scope?.let { values[scope.id] != null } ?: false

    private val values: MutableMap<String, T> = ConcurrentHashMap()

    override fun release(scope: ScopeInstance?) {
        scope?.let {
            if (logger.level == Level.DEBUG) {
                logger.debug("releasing '$scope' ~ $beanDefinition ")
            }
            values.remove(scope.id)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(scope: ScopeInstance?, parameters: ParametersDefinition?): T {
        if (scope == null) throw ScopeNotCreatedException("No scope instance when trying to resolve $beanDefinition")
        val internalId = scope.id
        var current = values[internalId]
        if (current == null) {
            current = create(beanDefinition, scope, parameters)
            values[internalId] = current ?: error("DefaultInstance creation from $beanDefinition should not be null")
        }
        return current as T
    }
}