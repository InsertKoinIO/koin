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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeId
import org.koin.ext.getFullName
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Bean Registry
 * declare/find definitions
 *
 * @author Arnaud Giuliani
 */
class BeanRegistry {

    private val definitions: HashSet<BeanDefinition<*>> = hashSetOf()
    private val definitionsNames: MutableMap<String, BeanDefinition<*>> = ConcurrentHashMap()
    private val definitionsClass: MutableMap<KClass<*>, BeanDefinition<*>> = ConcurrentHashMap()

    /**
     * retrieve all definitions
     * @return definitions
     */
    fun getAllDefinitions(): Set<BeanDefinition<*>> = definitions

    /**
     * Load definitions from a Module
     * @param koin instance
     * @param modules
     */
    fun loadModules(koin: Koin, vararg modules: Module) {
        modules.forEach { module: Module ->
            saveDefinitions(module)
            linkContext(module, koin)
        }
        logger.info(
            "registered ${definitions.size} definitions"
        )
    }

    /**
     * Load definitions from a Module
     * @param koin instance
     * @param modules
     */
    fun loadModules(koin: Koin, modules: List<Module>) {
        modules.forEach { module: Module ->
            saveDefinitions(module)
            linkContext(module, koin)
        }
        logger.info(
            "registered ${definitions.size} definitions"
        )
    }

    private fun saveDefinitions(module: Module) {
        module.definitions.forEach { definition ->
            saveDefinition(definition)
        }
    }

    /**
     * Save a definition
     * @param definition
     */
    fun saveDefinition(definition: BeanDefinition<*>) {
        definitions.addDefinition(definition)
        if (definition.name != null) {
            saveDefinitionForName(definition)
        } else {
            saveDefinitionForTypes(definition)
        }
    }

    private fun HashSet<BeanDefinition<*>>.addDefinition(definition: BeanDefinition<*>) {
        val added = add(definition)
        if (!added && !definition.options.override) {
            throw DefinitionOverrideException("Already existing definition or try to override an existing one: $definition")
        }
    }

    private fun saveDefinitionForTypes(definition: BeanDefinition<*>) {
        saveDefinitionForType(definition.primaryType, definition)
        definition.secondaryTypes.forEach {
            saveDefinitionForType(it, definition)
        }
    }

    private fun saveDefinitionForType(type: KClass<*>, definition: BeanDefinition<*>) {
        if (definitionsClass[type] != null && !definition.options.override) {
            throw DefinitionOverrideException("Already existing definition or try to override an existing one with type '$type' and $definition but has already registered ${definitionsClass[type]}")
        } else {
            definitionsClass[type] = definition
            logger.info("bind type:'${type.getFullName()}' ~ $definition")
        }
    }

    private fun saveDefinitionForName(definition: BeanDefinition<*>) {
        definition.name?.let {
            if (definitionsNames[it] != null && !definition.options.override) {
                throw DefinitionOverrideException("Already existing definition or try to override an existing one with name '$it' with $definition but has already registered ${definitionsNames[it]}")
            } else {
                definitionsNames[it] = definition
                logger.info("bind name:'${definition.name}' ~ $definition")
            }
        }
    }

    private fun linkContext(it: Module, koin: Koin) {
        it.koin = koin
    }

    /**
     * Find a definition
     * @param name
     * @param clazz
     */
    fun findDefinition(
        name: String? = null,
        clazz: KClass<*>
    ): BeanDefinition<*>? =
        name?.let { findDefinitionByName(name) } ?: findDefinitionByClass(clazz)

    private fun findDefinitionByClass(kClass: KClass<*>): BeanDefinition<*>? {
        return definitionsClass[kClass]
    }

    private fun findDefinitionByName(name: String): BeanDefinition<*>? {
        return definitionsNames[name]
    }

    internal fun findAllCreatedAtStartDefinition(): List<BeanDefinition<*>> {
        val effectiveInstances = definitionsClass.values + definitionsNames.values
        return effectiveInstances.distinct().filter { it.options.isCreatedAtStart }
    }

    internal fun releaseInstanceForScope(scope: Scope) {
        definitions.filter { it.getScopeId() == scope.id }.forEach { it.instance.release(scope) }
    }

    /**
     * Total number of definitions
     */
    fun size() = definitions.size

    /**
     * Retrieve a definition
     * @param clazz
     */
    fun getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
        return definitions.firstOrNull {
            it.primaryType == clazz || it.secondaryTypes.contains(
                clazz
            )
        }
    }

    fun close() {
        definitions.clear()
        definitionsNames.clear()
        definitionsClass.clear()
    }
}

