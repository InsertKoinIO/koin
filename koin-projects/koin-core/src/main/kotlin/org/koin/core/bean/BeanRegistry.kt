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
class BeanRegistry() {

    val definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(def: BeanDefinition<*>, path: Path) {
        val definition = def.copy(path = path)
        val existingBean = definitions.firstOrNull { it == definition }

        val isOverriding = existingBean != null
        if (isOverriding && !definition.allowOverride) {
            throw BeanOverrideException("Try to override definition $existingBean with $definition, but override is not allowed. Use 'override' option in your definition or module.")
        }

        existingBean?.let {
            definitions.remove(existingBean)
        }
        definitions += definition

        val kw = if (isOverriding) "override" else "declare"
        Koin.logger.info("[module] $kw $definition")
    }

    /**
     * Search bean by its canonicalName, respectfully to requested type.
     */
    fun search(name: String, clazz: KClass<*>): List<BeanDefinition<*>> =
        searchDefinition { filterByNameAndClass(it, name, clazz) }

    fun filterByNameAndClass(it: BeanDefinition<*>, name: String, clazz: KClass<*>): Boolean =
        it.name == name && filterByClass(it, clazz)

    fun filterByNameAndClass(it: BeanDefinition<*>, name: String, clazz: Class<*>): Boolean =
        it.name == name && filterByClass(it, clazz)

    /**
     * Search by name and class
     */
    fun search(name: String, clazz: Class<*>): List<BeanDefinition<*>> =
        searchDefinition { it.name == name && (it.clazz.java == clazz || it.types.map { it.java }.contains(clazz)) }

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: KClass<*>): List<BeanDefinition<*>> {
        return searchDefinition { filterByClass(it, clazz) }
    }

    fun filterByClass(it: BeanDefinition<*>, clazz: KClass<*>): Boolean =
        it.clazz == clazz || it.types.contains(clazz)

    fun filterByClass(it: BeanDefinition<*>, clazz: Class<*>): Boolean =
        it.clazz.java == clazz || it.types.map { it.java }.contains(clazz)

    /**
     * Search definition with given filter function
     */
    private fun searchDefinition(filter: (BeanDefinition<*>) -> Boolean): List<BeanDefinition<*>> =
        definitions.filter(filter)

    /**
     * Get bean definitions from given path
     */
    fun getDefinitions(paths: Set<Path>): List<BeanDefinition<*>> {
        return definitions.filter { def -> definitions.first { it == def }.path in paths }
    }


    /**
     * Retrieve bean definition
     * @param clazzName - class canonicalName
     * @param modulePath - Module path
     * @param definitionResolver - function to find bean definition
     * @param lastInStack - to check visibility with last bean in stack
     */
    fun getVisibleBean(
        clazzName: String,
        modulePath: Path? = null,
        definitionResolver: () -> List<BeanDefinition<*>>,
        lastInStack: BeanDefinition<*>?
    ): BeanDefinition<*> {
        val candidates: List<BeanDefinition<*>> = (if (lastInStack != null) {
            val found = definitionResolver()
            val filteredByVisibility = found.filter { lastInStack.canSee(it) }
            if (found.isNotEmpty() && filteredByVisibility.isEmpty()) {
                throw NotVisibleException("Can't proceedResolution '$clazzName' - Definition is not visible from last definition : $lastInStack")
            }
            filteredByVisibility
        } else {
            definitionResolver()
        }).distinct()

        val filteredCandidates =
            if (modulePath != null) candidates.filter { it.path.isVisible(modulePath) } else candidates

        return when {
            filteredCandidates.size == 1 -> filteredCandidates.first()
            filteredCandidates.isEmpty() -> throw NoBeanDefFoundException("No compatible definition found for type '$clazzName'. Check your module definition")
            else -> throw DependencyResolutionException(
                "Multiple definitions found for type '$clazzName' - Koin can't choose between :\n\t${candidates.joinToString(
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