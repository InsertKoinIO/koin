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
import org.koin.core.instance.DefinitionFilter
import org.koin.core.name
import org.koin.core.scope.Scope
import org.koin.core.scope.isVisibleToScope
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.path.Path
import org.koin.error.BeanOverrideException
import org.koin.error.DependencyResolutionException
import org.koin.error.NoBeanDefFoundException
import org.koin.error.NotVisibleException
import kotlin.reflect.KClass


/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 *
 * @author - Arnaud GIULIANI
 */
class BeanRegistry {
    private val definitionsByClass = hashMapOf<KClass<*>, HashSet<BeanDefinition<*>>>()
    private val definitionsByNameAndClass =
        hashMapOf<String, HashMap<KClass<*>, HashSet<BeanDefinition<*>>>>()

    val definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(definition: BeanDefinition<*>) {
        val isOverriding = definitions.remove(definition)

        if (isOverriding && !definition.allowOverride) {
            throw BeanOverrideException("Try to override definition with $definition, but override is not allowed. Use 'override' option in your definition or module.")
        }

        definitions += definition

        cacheDefinitionByClass(definition)
        cacheDefinitionByNameAndClass(definition)

        val kw = if (isOverriding) "override" else "declare"
        Koin.logger.info("[module] $kw $definition")
    }

    private fun cacheDefinitionByClass(definition: BeanDefinition<*>) {
        definition.classes.forEach {
            val definitionSet = definitionsByClass.getOrPut(it) { hashSetOf() }
            definitionSet.remove(definition)
            definitionSet.add(definition)
        }
    }

    private fun cacheDefinitionByNameAndClass(definition: BeanDefinition<*>) {
        if (definition.name.isNotEmpty()) {
            val classMap = definitionsByNameAndClass.getOrPut(definition.name) { hashMapOf() }
            definition.classes.forEach {
                val definitionSet = classMap.getOrPut(it) { hashSetOf() }
                definitionSet.remove(definition)
                definitionSet.add(definition)
            }
        }
    }

    fun searchByClass(
        clazz: KClass<*>,
        filterFunction: DefinitionFilter? = null
    ): List<BeanDefinition<*>> {
        val candidatesByClass = definitionsByClass[clazz]

        val filteredCandidates = (if (filterFunction != null) {
            candidatesByClass?.filter(filterFunction)
        } else candidatesByClass) ?: emptyList()

        return filteredCandidates.filter { clazz in it.classes }
    }

    fun searchByNameAndClass(
        name: String,
        clazz: KClass<*>,
        filterFunction: DefinitionFilter? = null
    ): List<BeanDefinition<*>> {
        val candidatesByNameAndClass = definitionsByNameAndClass[name]?.get(clazz)

        val filteredCandidates = (if (filterFunction != null) {
            candidatesByNameAndClass?.filter(filterFunction)
        } else candidatesByNameAndClass) ?: emptyList()

        return filteredCandidates.filter { clazz in it.classes }
    }

    /**
     * Get bean definitions from given path
     */
    fun getDefinitionsInPaths(paths: Set<Path>): List<BeanDefinition<*>> {
        return definitions.filter { def -> definitions.first { it == def }.path in paths }
    }


    @Suppress("UNCHECKED_CAST")
            /**
             * Retrieve bean definition
             * @param clazzName - class canonicalName
             * @param modulePath - Module path
             * @param definitionResolver - function to find bean definition
             * @param lastInStack - to check visibility with last bean in stack
             */
    fun <T> retrieveDefinition(
        clazz: KClass<*>,
        scope: Scope?,
        definitionResolver: () -> List<BeanDefinition<*>>,
        lastInStack: BeanDefinition<*>?
    ): BeanDefinition<T> {
        val candidates: List<BeanDefinition<*>> = (if (lastInStack != null) {
            val found = definitionResolver()
            val filteredByVisibility = found.filter { lastInStack.canSee(it) }
            if (found.isNotEmpty() && filteredByVisibility.isEmpty()) {
                throw NotVisibleException("Can't proceedResolution '$clazz' - Definition is not visible from last definition : $lastInStack")
            }
            filteredByVisibility
        } else {
            definitionResolver()
        }).distinct()

        val filterByScope = if (scope != null){
            candidates.filter { it.isVisibleToScope(scope) }
        } else candidates

        return when {
            filterByScope.size == 1 -> filterByScope.first() as BeanDefinition<T>
            filterByScope.isEmpty() -> throw NoBeanDefFoundException("No compatible definition found for type '${clazz.name()}'. Check your module definition")
            else -> throw DependencyResolutionException(
                "Multiple definitions found for type '$clazz' - Koin can't choose between :\n\t${filterByScope.joinToString(
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
    }
}
