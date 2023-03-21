/*
 * Copyright 2017-2023 the original author or authors.
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
import org.koin.mp.KoinPlatformTools

/**
 * Single instance holder
 * @author Arnaud Giuliani
 */
class SingleInstanceFactory<T>(beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(beanDefinition) {

    private var value: T? = null

    private fun getValue() : T = value ?: error("Single instance created couldn't return value")

    override fun isCreated(context: InstanceContext?): Boolean = (value != null)

    override fun drop(scope: Scope?) {
        beanDefinition.callbacks.onClose?.invoke(value)
        value = null
    }

    override fun dropAll(){
        drop()
    }

    override fun create(context: InstanceContext): T {
        return if (value == null) {
            super.create(context)
        } else getValue()
    }

    override fun get(context: InstanceContext): T {
        KoinPlatformTools.synchronized(this) {
            if (!isCreated(context)) {
                value = create(context)
            }
        }
        return getValue()
    }
}