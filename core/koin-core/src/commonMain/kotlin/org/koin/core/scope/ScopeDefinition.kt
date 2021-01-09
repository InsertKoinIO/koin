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
package org.koin.core.scope

import org.koin.core.component.KoinApiExtension
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier._q
import kotlin.reflect.KClass

/**
 * Internal Scope Definition
 */
data class ScopeDefinition(val qualifier: Qualifier, val isRoot: Boolean = false) {

    @KoinApiExtension
    val definitions: HashSet<BeanDefinition<*>> = hashSetOf()
    
    fun save(beanDefinition: BeanDefinition<*>, forceOverride: Boolean = false) {
        if (definitions.contains(beanDefinition)) {
            if (beanDefinition.options.override || forceOverride) {
                definitions.remove(beanDefinition)
            } else {
                val current = definitions.firstOrNull { it == beanDefinition }
                throw DefinitionOverrideException(
                        "Definition '$beanDefinition' try to override existing definition. Please use override option or check for definition '$current'")
            }
        }
        definitions.add(beanDefinition)
    }

    fun remove(beanDefinition: BeanDefinition<*>) {
        definitions.remove(beanDefinition)
    }

    internal fun size() = definitions.size

    inline fun <reified T : Any> declareNewDefinition(
            instance: T,
            defQualifier: Qualifier? = null,
            secondaryTypes: List<KClass<*>>? = null,
            override: Boolean = false
    ): BeanDefinition<out Any?> {
        val clazz = T::class
        val found: BeanDefinition<*>? =
                definitions.firstOrNull { def -> def.`is`(clazz, defQualifier, this.qualifier) }
        if (found != null) {
            if (override) {
                remove(found)
            } else {
                throw DefinitionOverrideException(
                        "Trying to override existing definition '$found' with new definition typed '$clazz'")
            }
        }
        val beanDefinition = Definitions.createSingle(
                clazz,
                defQualifier,
                { instance },
                Options(isCreatedAtStart = false, override = override, isExtraDefinition = true),
                secondaryTypes ?: emptyList(),
                qualifier,
        )
        save(beanDefinition, override)
        return beanDefinition
    }

    internal fun unloadDefinition(beanDefinition: BeanDefinition<*>) {
        definitions.remove(beanDefinition)
    }

    internal fun removeExtras() {
        val extras = definitions.filter { it.options.isExtraDefinition }
        definitions.removeAll(extras)
    }

    companion object {
        const val ROOT_SCOPE_ID = "-Root-"
        val ROOT_SCOPE_QUALIFIER = _q(ROOT_SCOPE_ID)
        internal fun rootDefinition() = ScopeDefinition(ROOT_SCOPE_QUALIFIER, isRoot = true)
    }
}