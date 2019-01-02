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
package org.koin.core.scope

import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.bean.Options
import org.koin.core.instance.ScopedInstance
import org.koin.core.module.Module

data class ScopeDefinition(val name: String, val module : Module) {

    var definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Declare a ScopeInstance definition
     * @param scopeKey
     * @param name
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> scoped(
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition.createScope(name, name, definition)
        declare(beanDefinition, override)
        return beanDefinition
    }

    inline fun <reified T> declare(beanDefinition: BeanDefinition<T>, override: Boolean) {
        module.declareDefinition(beanDefinition, Options(override = override))
        definitions.add(beanDefinition)
    }

    internal fun release(instance: ScopeInstance) {
        definitions.filter { it is ScopedInstance<*> }.forEach { it.instance.release(instance) }
    }
}