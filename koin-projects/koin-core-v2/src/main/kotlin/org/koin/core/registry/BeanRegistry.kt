package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.AlreadyExistingDefinition
import org.koin.core.error.OverrideDefinitionException
import org.koin.core.module.Module
import kotlin.reflect.KClass

class BeanRegistry {

    internal val definitions = hashSetOf<BeanDefinition<*>>()
    private val definitionsNames = hashMapOf<String, BeanDefinition<*>>()
    private val definitionsClass = hashMapOf<KClass<*>, BeanDefinition<*>>()

    fun loadModules(koin: Koin, vararg modulesToLoad: Module) {
        modulesToLoad.forEach { module: Module ->
            saveDefinitions(module)
            linkContext(module, koin)
        }
        KoinApplication.log("[Koin] ${definitions.size} definitions")
    }

    private fun saveDefinitions(module: Module) {
        module.definitions.forEach { definition ->
            saveDefinition(definition)
        }
    }

    fun saveDefinition(definition: BeanDefinition<*>) {
        val added = definitions.add(definition)
        if (!added) {
            throw AlreadyExistingDefinition("Already existing definition : $definition")
        } else {
            if (definition.name != null) {
                saveDefinitionForName(definition)
            } else {
                saveDefinitionForTypes(definition)
            }
        }
    }

    private fun saveDefinitionForTypes(definition: BeanDefinition<*>) {
        saveDefinitionForType(definition.primaryType, definition)
        definition.secondaryTypes.forEach {
            saveDefinitionForType(it, definition)
        }
    }

    private fun saveDefinitionForType(type: KClass<*>, definition: BeanDefinition<*>) {
        if (definitionsClass[type] != null) {
            throw OverrideDefinitionException("Try to override definition type '$type' with $definition but has already registered ${definitionsClass[type]}")
        } else {
            definitionsClass[type] = definition
            KoinApplication.log("[Koin] bind type:'$type' ~ $definition")
        }
    }

    private fun saveDefinitionForName(definition: BeanDefinition<*>) {
        definition.name?.let {
            if (definitionsNames[it] != null) {
                throw OverrideDefinitionException("Try to override definition name '$it' with $definition but has already registered ${definitionsNames[it]}")
            } else {
                definitionsNames[it] = definition
                KoinApplication.log("[Koin] bind name:'${definition.name}' ~ $definition")
            }
        }
    }

    private fun linkContext(it: Module, koin: Koin) {
        it.koin = koin
    }

    fun findDefinition(
        name: String?,
        clazz: KClass<*>
    ): BeanDefinition<*>? =
        name?.let { findDefinitionByName(name) } ?: findDefinitionByClass(clazz)

    private fun findDefinitionByClass(kClass: KClass<*>): BeanDefinition<*>? {
        return definitionsClass[kClass]
    }

    private fun findDefinitionByName(name: String): BeanDefinition<*>? {
        return definitionsNames[name]
    }
}