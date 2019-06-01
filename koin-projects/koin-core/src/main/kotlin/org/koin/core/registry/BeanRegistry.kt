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

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.definition.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
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
    private val definitionsPrimaryTypes: MutableMap<KClass<*>, BeanDefinition<*>> = ConcurrentHashMap()
    private val definitionsSecondaryTypes: MutableMap<KClass<*>, ArrayList<BeanDefinition<*>>> = ConcurrentHashMap()
    private val definitionsToCreate: HashSet<BeanDefinition<*>> = hashSetOf()

    /**
     * Load definitions from a Module
     * @param modules
     */
    fun loadModules(modules: Iterable<Module>) {
        modules.forEach { module: Module ->
            saveDefinitions(module)
        }
    }

    /**
     * unload definitions from a Module
     * @param modules
     */
    internal fun unloadModules(modules: Iterable<Module>) {
        modules.forEach { module: Module ->
            removeDefinitions(module)
        }
    }

    private fun removeDefinitions(module: Module) {
        module.definitions.forEach { definition ->
            removeDefinition(definition)
        }
    }

    private fun saveDefinitions(module: Module) {
        module.definitions.forEach { definition ->
            saveDefinition(definition)
        }
    }

    /**
     * retrieve all definitions
     * @return definitions
     */
    fun getAllDefinitions(): Set<BeanDefinition<*>> = definitions

    /**
     * Save a definition
     * @param definition
     */
    private fun removeDefinition(definition: BeanDefinition<*>) {
        definition.instance?.close()
        definitions.remove(definition)
        if (definition.qualifier != null) {
            removeDefinitionForName(definition)
        } else {
            removeDefinitionForTypes(definition)
        }
        removeDefinitionForSecondaryTypes(definition)
    }

    /**
     * Save a definition
     * @param definition
     */
    fun saveDefinition(definition: BeanDefinition<*>) {
        definitions.addDefinition(definition)
        definition.createInstanceHolder()
        if (definition.qualifier != null) {
            saveDefinitionForName(definition)
        } else {
            saveDefinitionForTypes(definition)
        }
        if (definition.secondaryTypes.isNotEmpty()) {
            saveDefinitionForSecondaryTypes(definition)
        }
        if (definition.options.isCreatedAtStart) {
            saveDefinitionForStart(definition)
        }
    }

    private fun saveDefinitionForSecondaryTypes(definition: BeanDefinition<*>) {
        definition.secondaryTypes.forEach {
            saveDefinitionForSecondaryType(definition, it)
        }
    }

    private fun saveDefinitionForSecondaryType(definition: BeanDefinition<*>, type: KClass<*>) {
        val secondaryTypeDefinitions: ArrayList<BeanDefinition<*>> = definitionsSecondaryTypes[type]
                ?: createSecondaryType(type)

        secondaryTypeDefinitions.add(definition)
        if (logger.isAt(Level.INFO)) {
            logger.info("bind secondary type:'${type.getFullName()}' ~ $definition")
        }
    }

    private fun createSecondaryType(type: KClass<*>): ArrayList<BeanDefinition<*>> {
        definitionsSecondaryTypes[type] = arrayListOf()
        return definitionsSecondaryTypes[type]!!
    }

    private fun saveDefinitionForStart(definition: BeanDefinition<*>) {
        definitionsToCreate.add(definition)
    }

    private fun HashSet<BeanDefinition<*>>.addDefinition(definition: BeanDefinition<*>) {
        val added = add(definition)
        if (!added && !definition.options.override) {
            throw DefinitionOverrideException("Already existing definition or try to override an existing one: $definition")
        }
    }

    private fun saveDefinitionForTypes(definition: BeanDefinition<*>) {
        saveDefinitionForType(definition.primaryType, definition)
    }

    private fun removeDefinitionForSecondaryTypes(definition: BeanDefinition<*>) {
        definition.secondaryTypes.forEach {
            removeDefinitionForSecondaryType(definition, it)
        }
    }

    private fun removeDefinitionForSecondaryType(definition: BeanDefinition<*>, type: KClass<*>) {
        val removed = definitionsSecondaryTypes[type]?.remove(definition) ?: false
        if (logger.isAt(Level.DEBUG) && removed) {
            logger.info("unbind secondary type:'${type.getFullName()}' ~ $definition")
        }
    }

    private fun removeDefinitionForTypes(definition: BeanDefinition<*>) {
        val key = definition.primaryType
        if (definitionsPrimaryTypes[key] == definition) {
            definitionsPrimaryTypes.remove(key)
            if (logger.isAt(Level.DEBUG)) {
                logger.info("unbind type:'${key.getFullName()}' ~ $definition")
            }
        }
    }

    private fun saveDefinitionForType(type: KClass<*>, definition: BeanDefinition<*>) {
        if (definitionsPrimaryTypes[type] != null && !definition.options.override) {
            throw DefinitionOverrideException("Already existing definition or try to override an existing one with type '$type' and $definition but has already registered ${definitionsPrimaryTypes[type]}")
        } else {
            definitionsPrimaryTypes[type] = definition
            if (logger.isAt(Level.INFO)) {
                logger.info("bind type:'${type.getFullName()}' ~ $definition")
            }
        }
    }

    private fun removeDefinitionForName(definition: BeanDefinition<*>) {
        definition.qualifier?.let {
            val key = it.toString()
            if (definitionsNames[key] == definition) {
                definitionsNames.remove(key)
                if (logger.isAt(Level.DEBUG)) {
                    logger.info("unbind qualifier:'$key' ~ $definition")
                }
            }
        }
    }

    private fun saveDefinitionForName(definition: BeanDefinition<*>) {
        definition.qualifier?.let {
            if (definitionsNames[it.toString()] != null && !definition.options.override) {
                throw DefinitionOverrideException("Already existing definition or try to override an existing one with qualifier '$it' with $definition but has already registered ${definitionsNames[it.toString()]}")
            } else {
                definitionsNames[it.toString()] = definition
                if (logger.isAt(Level.INFO)) {
                    logger.info("bind qualifier:'${definition.qualifier}' ~ $definition")
                }
            }
        }
    }

    /**
     * Find a definition
     * @param qualifier
     * @param clazz
     */
    fun findDefinition(
            qualifier: Qualifier? = null,
            clazz: KClass<*>
    ): BeanDefinition<*>? {
        return if (qualifier != null) {
            findDefinitionByName(qualifier.toString())
        } else {
            findDefinitionByType(clazz) ?: findDefinitionBySecondaryType(clazz)
        }
    }

    //TODO Find with secondary type

    private fun findDefinitionByType(kClass: KClass<*>): BeanDefinition<*>? {
        return definitionsPrimaryTypes[kClass]
    }

    private fun findDefinitionBySecondaryType(kClass: KClass<*>): BeanDefinition<*>? {
        val foundTypes = definitionsSecondaryTypes[kClass]
        return when {
            foundTypes != null && foundTypes.size == 1 -> foundTypes[0]
            foundTypes != null && foundTypes.size > 1 -> throw NoBeanDefFoundException("Found multiple definitions for type '${kClass.getFullName()}': $foundTypes. Please use the 'bind<P,S>()' function to bind your instance from primary and secondary types.")
            else -> null
        }
    }

    private fun findDefinitionByName(name: String): BeanDefinition<*>? {
        return definitionsNames[name]
    }

    internal fun findAllCreatedAtStartDefinition(): Set<BeanDefinition<*>> {
        return definitionsToCreate
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
        definitions.forEach { it.close() }
        definitions.clear()
        definitionsNames.clear()
        definitionsPrimaryTypes.clear()
        definitionsToCreate.clear()
    }

    /**
     * Find all definition compatible with given type
     */
    fun getDefinitionsForClass(clazz: KClass<*>) = getAllDefinitions()
            .filter { it.primaryType == clazz || it.secondaryTypes.contains(clazz) && !it.hasScopeSet() }
}
