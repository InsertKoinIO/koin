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
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.mp.KoinPlatformTools

/**
 * Single instance holder
 * @author Arnaud Giuliani
 */
class ScopedInstanceFactory<T>(beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(beanDefinition) {

    private var values = hashMapOf<ScopeID, T>()

    override fun isCreated(context: InstanceContext?): Boolean = (values[context?.scope?.id] != null)

    override fun drop(scope: Scope?) {
        scope?.let {
            beanDefinition.callbacks.onClose?.invoke(values[it.id])
            values.remove(it.id)
        }
    }

    override fun create(context: InstanceContext): T {
        return if (values[context.scope.id] == null) {
            super.create(context)
        } else {
            values[context.scope.id] ?: error("Scoped instance not found for ${context.scope.id} in $beanDefinition")
        }
    }

    override fun get(context: InstanceContext): T {
        if (context.scope.scopeQualifier != beanDefinition.scopeQualifier) {
            error("Wrong Scope: trying to open instance for ${context.scope.id} in $beanDefinition")
        }
        KoinPlatformTools.synchronized(this) {
            if (!isCreated(context)) {
                values[context.scope.id] = create(context)
            }
        }
        return values[context.scope.id] ?: error("Scoped instance not found for ${context.scope.id} in $beanDefinition")
    }

    override fun dropAll() {
        values.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun refreshInstance(scopeID: ScopeID, instance: Any) {
        values[scopeID] = instance as T
    }
}
