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
import org.koin.core.error.MissingScopeValueException
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.mp.KoinPlatformTools

/**
 * Scope instance holder
 * @author Arnaud Giuliani
 */
import java.util.concurrent.ConcurrentHashMap

class ScopedInstanceFactory<T>(beanDefinition: BeanDefinition<T>, val holdInstance : Boolean = true) :
    InstanceFactory<T>(beanDefinition) {

    private val values = ConcurrentHashMap<ScopeID, T>()

    fun size(): Int = values.size

    @PublishedApi
    internal fun saveValue(id: ScopeID, value: T) {
        values[id] = value
    }

    override fun isCreated(context: ResolutionContext?): Boolean = (values[context?.scope?.id] != null)

    override fun drop(scope: Scope?) {
        scope?.let {
            val removedValue = values.remove(it.id)
            beanDefinition.callbacks.onClose?.invoke(removedValue)
        }
    }

    override fun create(context: ResolutionContext): T {
        return values[context.scope.id] ?: super.create(context)
    }

    override fun get(context: ResolutionContext): T {
        if (context.scope.scopeQualifier != beanDefinition.scopeQualifier && context.scopeArchetype != beanDefinition.scopeQualifier) {
            error("Wrong Scope qualifier: trying to open instance for ${context.scope.id} in $beanDefinition")
        }
        if (!isCreated(context) && holdInstance) {
            values[context.scope.id] = super.create(context)
        }
        return values[context.scope.id] ?: throw MissingScopeValueException("Factory.get -Scoped instance not found for ${context.scope.id} in $beanDefinition")
    }

    override fun dropAll() {
        values.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun refreshInstance(scopeID: ScopeID, instance: Any) {
        values[scopeID] = instance as T
    }
}
