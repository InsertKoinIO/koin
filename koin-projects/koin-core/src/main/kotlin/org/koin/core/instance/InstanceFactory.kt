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

import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.definition.BeanDefinitionId

/**
 * Instance factory - handle objects creation against BeanRegistry
 * @author - Arnaud GIULIANI
 */
open class InstanceFactory {

    val instanceHolders = HashMap<BeanDefinitionId, InstanceHolder<*>>()

    /**
     * Retrieve or create bean instance
     * @return Instance / has been created
     */
    fun <T> retrieveInstance(def: BeanDefinition<T>, p: ParameterDefinition): Instance<T> {
        // find holder
        var holder = find(def)
        if (holder == null) {
            holder = create(def)
            // save it
            instanceHolders[def.id] = holder
        }
        return holder.get(p)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> find(def: BeanDefinition<T>): InstanceHolder<T>? = instanceHolders[def.id] as? InstanceHolder<T>

    open fun <T> create(def: BeanDefinition<T>): InstanceHolder<T> {
        return if (def.isSingleton) SingleInstanceHolder(def)
        else FactoryInstanceHolder(def)
    }

    fun release(definitions: List<BeanDefinition<*>>) {
        definitions.forEach { release(it) }
    }

    fun release(definition: BeanDefinition<*>) {
        instanceHolders.remove(definition.id)?.delete()
    }

    /**
     * Clear all resources
     */
    fun clear() {
        instanceHolders.clear()
    }
}