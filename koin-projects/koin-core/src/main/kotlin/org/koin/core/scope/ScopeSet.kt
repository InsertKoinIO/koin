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

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.ScopeDefinitionInstance
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

data class ScopeSet(val qualifier: Qualifier, val module: Module) {

    val definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Declare a ScopeInstance definition
     * @param name
     * @param override
     * @param definition - definition function
     */
    inline fun <reified T> scoped(
            name: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = DefinitionFactory.createScoped(name, qualifier, definition)
        module.declareDefinition(beanDefinition, Options(override = override))
        definitions.add(beanDefinition)
        return beanDefinition
    }

    @Deprecated("Single definition can't be used in a scope", level = DeprecationLevel.ERROR)
    inline fun <reified T> single(
            qualifier: Qualifier? = null,
            createdAtStart: Boolean = false,
            override: Boolean = false,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        error("Single definition can't be used in a scope")
    }

    @Deprecated("Factory definition can't be used in a scope", level = DeprecationLevel.ERROR)
    inline fun <reified T> factory(
            qualifier: Qualifier? = null,
            override: Boolean = false,
            noinline definition: Definition<T>
    ): BeanDefinition<T> {
        error("Factory definition can't be used in a scope")
    }

    internal fun release(instance: Scope) {
        definitions
                .filter { it.instance is ScopeDefinitionInstance<*> }
                .forEach { it.instance?.release(InstanceContext(scope = instance)) }
    }

    override fun toString(): String {
        return "Scope['$qualifier']"
    }
}