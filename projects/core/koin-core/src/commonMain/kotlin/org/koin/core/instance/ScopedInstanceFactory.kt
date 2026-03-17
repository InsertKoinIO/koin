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
class ScopedInstanceFactory<T>(beanDefinition: BeanDefinition<T>, val holdInstance : Boolean = true) :
    InstanceFactory<T>(beanDefinition) {

    private var values = KoinPlatformTools.safeHashMap<ScopeID, T>()

    fun size() = values.size

    @PublishedApi
    internal fun saveValue(id : ScopeID, value : T){
        values[id] = value
    }

    override fun isCreated(context: ResolutionContext?): Boolean {
        val scopeId = context?.scope?.id
        return if (scopeId == null) {
            // When no specific context is provided, report if any instance exists for any scope
            values.isNotEmpty()
        } else {
            values[scopeId] != null
        }
    }

    override fun drop(scope: Scope?) {
        scope?.let { s ->
            KoinPlatformTools.synchronized(this) {
                val v = values.remove(s.id)
                beanDefinition.callbacks.onClose?.invoke(v)
            }
        }
    }

    override fun create(context: ResolutionContext): T {
        val existing = values[context.scope.id]
        return if (existing != null) {
            existing
        } else {
            val created = super.create(context)
            if (holdInstance) {
                values[context.scope.id] = created
            }
            created
        }
    }

    override fun get(context: ResolutionContext): T {
        if (context.scope.scopeQualifier != beanDefinition.scopeQualifier && context.scopeArchetype != beanDefinition.scopeQualifier) {
            error("Wrong Scope qualifier: trying to open instance for ${context.scope.id} in $beanDefinition")
        }

        // Fast path: check if already created without locking
        val existing = values[context.scope.id]
        if (existing != null) {
            return existing
        }

        // Slow path: need to create the instance with locking
        return KoinPlatformTools.synchronized(this) {
            // Double-check inside lock to avoid race condition
            val doubleCheck = values[context.scope.id]
            if (doubleCheck != null) {
                doubleCheck
            } else {
                if (!holdInstance) {
                    throw MissingScopeValueException("No value for scope '${context.scope.id}' in $beanDefinition")
                }
                val created = super.create(context)
                if (holdInstance) {
                    values[context.scope.id] = created
                }
                created
            }
        }
    }

    override fun dropAll() {
        KoinPlatformTools.synchronized(this) {
            if (values.isNotEmpty()) {
                val onClose = beanDefinition.callbacks.onClose
                if (onClose != null) {
                    // Invoke onClose for each stored instance
                    values.values.forEach { v -> onClose.invoke(v) }
                }
                values.clear()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun refreshInstance(scopeID: ScopeID, instance: Any) {
        KoinPlatformTools.synchronized(this) {
            values[scopeID] = instance as T
        }
    }
}
