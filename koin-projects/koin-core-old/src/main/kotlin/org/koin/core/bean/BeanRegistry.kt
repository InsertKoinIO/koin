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
import org.koin.core.instance.InstanceRequest
import org.koin.core.scope.Scope
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.BeanOverrideException
import org.koin.ext.fullname
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
    private val definitionsByNames = hashMapOf<String, BeanDefinition<*>>()
    private val definitionByClassNames = hashMapOf<String, ArrayList<BeanDefinition<*>>>()

    /**
     * Remove existing definition
     * @param definition
     */
    fun remove(definition: BeanDefinition<*>) {
        definitions.remove(definition)

        removeDefinitionFromItsName(definition)

        removeDefinitionForItsTypes(definition)
    }

    /**
     * extract class name
     * @param clazz
     */
    fun getClazzName(clazz : KClass<*>) = clazz.fullname()

    private fun removeDefinitionForItsTypes(definition: BeanDefinition<*>) {
        val types: List<String> = definition.classes.map { getClazzName(it) }
        types.forEach {
            val set = definitionByClassNames[it]
            set?.remove(definition)
            definitionByClassNames[it] = set ?: arrayListOf()
        }
    }

    private fun removeDefinitionFromItsName(definition: BeanDefinition<*>) {
        if (definitionsByNames[definition.name] == definition) {
            definitionsByNames.remove(definition.name)
        }
    }

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(definition: BeanDefinition<*>) {
        val isOverriding = isAnOverridingDefinition(definition)
        if (isOverriding) {
            remove(definition)
        }
        saveDefinition(definition, isOverriding)
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

        if (definition.name.isNotEmpty()) {
            saveDefinitionByName(definition)
        }

        saveDefinitionByTypes(definition)

        val kw = if (isOverriding) "override" else "declare"
        Koin.logger.info("[definition] $kw $definition")
    }

    private fun saveDefinitionByTypes(definition: BeanDefinition<*>) {
        definition.classes.map { getClazzName(it) }.forEach { saveDefinitionByClassName(it, definition) }
    }

    private fun saveDefinitionByClassName(className: String, definition: BeanDefinition<*>) {
        val list = definitionByClassNames[className] ?: arrayListOf()
        list.add(definition)
        definitionByClassNames[className] = list
    }

    private fun saveDefinitionByName(definition: BeanDefinition<*>) {
        if (definitionsByNames[definition.name] != null) {
            throw BeanOverrideException("Try to override name '${definition.name}' with definition $definition but already exists")
        } else {
            definitionsByNames[definition.name] = definition
        }
    }

    /**
     * Search definition against InstanceRequest
     * @param request
     */
    fun search(request: InstanceRequest): BeanDefinitionSearch {
        return when {
            request.name.isNotEmpty() -> {
                { searchByName(request.name) }
            }
            else -> {
                { searchByClass(request.clazz) }
            }
        }
    }

    /**
     * Search definitions by class
     */
    fun searchByClass(
        clazz: KClass<*>
    ): BeanDefinitionSearchResult {
        return definitionByClassNames[getClazzName(clazz)] ?: emptyList()
    }

    /**
     * Search definitions by name
     */
    fun searchByName(
        name: String
    ): BeanDefinitionSearchResult {
        return definitionsByNames[name]?.let { listOf(it) } ?: emptyList()
    }

    /**
     * Retrieve bean definition
     * @param clazzName - class canonicalName
     * @param modulePath - Module path
     * @param definitionResolver - function to find bean definition
     * @param lastInStack - to check visibility with last bean in stack
     */
    fun <T> findDefinition(
        scope: Scope?,
        search: BeanDefinitionSearch,
        lastInStack: BeanDefinition<*>?
    ): BeanDefinitionSearchResult {
        return search()
            .isVisibleToLastInStack(lastInStack)
            .isVisibleToScope(scope)
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

