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
package org.koin.core.bean

import org.koin.core.Koin
import org.koin.core.scope.Scope
import org.koin.core.scope.isVisibleToScope
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.BeanOverrideException
import org.koin.error.DependencyResolutionException
import org.koin.error.NoBeanDefFoundException
import org.koin.error.NotVisibleException
import org.koin.ext.name
import kotlin.reflect.KClass


/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 *
 * @author - Arnaud GIULIANI
 */
class BeanRegistry() {

    val definitions = hashSetOf<BeanDefinition<*>>()
    private val definitionsByNames = hashMapOf<String,BeanDefinition<*>>()
    private val definitionByClassNames = hashMapOf<String,ArrayList<BeanDefinition<*>>>()

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(definition: BeanDefinition<*>) {
        val isOverriding = isAnOverridingDefinition(definition)
        if (isOverriding){
            remove(definition)
        }
        saveDefinition(definition, isOverriding)
    }

    /**
     * Remove existing definition
     * @param definition
     */
    fun remove(definition: BeanDefinition<*>) {
        definitions.remove(definition)
        definitionsByNames.remove(definition.name)
        val list = definitionByClassNames[definition.primaryTypeName] ?: arrayListOf()
        list.remove(definition)
        definitionByClassNames[definition.primaryTypeName] = list
    }

    private fun isAnOverridingDefinition(definition: BeanDefinition<*>): Boolean {
        val isOverriding = definitions.contains(definition)
        if (isOverriding && !definition.allowOverride) {
            throw BeanOverrideException("Try to override definition with $definition, but override is not allowed. Use 'override' option in your definition or module.")
        }
        return isOverriding
    }

    private fun saveDefinition(
        definition: BeanDefinition<*>,
        isOverriding: Boolean
    ) {
        definitions += definition

        definitionsByNames[definition.name] = definition

        val list = definitionByClassNames[definition.primaryTypeName] ?: arrayListOf()
        list.add(definition)
        definitionByClassNames[definition.primaryTypeName] = list

        val kw = if (isOverriding) "override" else "declare"
        Koin.logger.info("[definition] $kw $definition")
    }

    fun searchByClass(
        clazz: KClass<*>
    ): List<BeanDefinition<*>> {
        return definitionByClassNames[clazz.name()] as List<BeanDefinition<*>>
    }

    // Name is unique
    fun searchByNameAndClass(
        name: String,
        clazz: KClass<*>
    ): List<BeanDefinition<*>> {
        return definitionsByNames[name]?.let { listOf(it) } ?: emptyList()
    }

    /**
     * Retrieve bean definition
     * @param clazzName - class canonicalName
     * @param modulePath - Module path
     * @param definitionResolver - function to find bean definition
     * @param lastInStack - to check visibility with last bean in stack
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> retrieveDefinition(
        scope: Scope?,
        definitionResolver: () -> List<BeanDefinition<*>>,
        lastInStack: BeanDefinition<*>?
    ): BeanDefinition<T> {
        val definitions = definitionResolver()
        val visibleDefinitions = filterByVisibility(lastInStack, definitions)
        val filterByScope = filterForScope(scope, visibleDefinitions)

        return checkedResult(filterByScope)
    }

    private fun filterByVisibility(
        lastInStack: BeanDefinition<*>?,
        definitions : List<BeanDefinition<*>>
    ): List<BeanDefinition<*>> {
        return if (lastInStack != null) {
            val filteredByVisibility = definitions.filter { lastInStack.isVisible(it) }
            if (definitions.isNotEmpty() && filteredByVisibility.isEmpty()) {
                throw NotVisibleException("Definition is not visible from last definition : $lastInStack")
            }
            filteredByVisibility
        }
        else definitions

    }

    private fun filterForScope(
        scope: Scope?,
        candidates: List<BeanDefinition<*>>
    ): List<BeanDefinition<*>> {
        return if (scope != null) {
            candidates.filter { it.isVisibleToScope(scope) }
        } else candidates
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> checkedResult(
        filterByScope: List<BeanDefinition<*>>
    ): BeanDefinition<T> {
        return when {
            filterByScope.size == 1 -> filterByScope.first() as BeanDefinition<T>
            filterByScope.isEmpty() -> throw NoBeanDefFoundException("No compatible definition found. Check your module definition")
            else -> throw DependencyResolutionException(
                "Multiple definitions found - Koin can't choose between :\n\t${filterByScope.joinToString(
                    "\n\t"
                )}\n\tCheck your modules definition, use inner modules visibility or definition names."
            )
        }
    }

    /**
     * Clear resources
     */
    fun clear() {
        definitions.clear()
        definitionsByNames.clear()
        definitionByClassNames.clear()
    }
}