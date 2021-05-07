/*
 * Copyright 2017-2021 the original author or authors.
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
import org.koin.mp.KoinPlatformTools.safeHashMap

/**
 * Single instance holder
 * @author Arnaud Giuliani
 */
class ScopedInstanceFactory<T>(beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(beanDefinition) {

    private var values = hashMapOf<ScopeID,T>()

    private fun getValueOrNull(scope : Scope) : T? = values[scope.id]
    private fun getValue(scope : Scope) : T = values[scope.id] ?: error("Scoped instance not found for ${scope.id}")

    override fun isCreated(context: InstanceContext?): Boolean = (values != null)

    override fun drop(scope: Scope?) {
        scope?.let {
            beanDefinition.callbacks.onClose?.invoke(values[it.id])
            values.remove(it.id)
        } ?: error("trying to drop scoped instance without scope")
    }

    override fun create(context: InstanceContext): T {
        return if (getValueOrNull(context.scope) == null) {
            super.create(context)
        } else getValue(context.scope)
    }

    override fun get(context: InstanceContext): T {
        KoinPlatformTools.synchronized(this) {
            if (!isCreated(context)) {
                values[context.scope.id] = create(context)
            }
        }
        return getValue(context.scope)
    }
}