package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.DefinitionAlreadyExistsException
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.Module
import kotlin.reflect.KClass

class BeanRegistry {

    internal val definitions: HashSet<BeanDefinition<*>> = hashSetOf()
    private val definitionsNames: HashMap<String, BeanDefinition<*>> = hashMapOf()
    private val definitionsClass: HashMap<KClass<*>, BeanDefinition<*>> = hashMapOf()

    fun loadModules(koin: Koin, vararg modulesToLoad: Module) {
        modulesToLoad.forEach { module: Module ->
            saveDefinitions(module)
            linkContext(module, koin)
        }
        KoinApplication.log("[Koin] registered ${definitions.size} definitions")
    }

    private fun saveDefinitions(module: Module) {
        module.definitions.forEach { definition ->
            saveDefinition(definition)
        }
    }

    private fun saveDefinition(definition: BeanDefinition<*>) {
        definitions.addDefinition(definition)
        if (definition.name != null) {
            saveDefinitionForName(definition)
        } else {
            saveDefinitionForTypes(definition)
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
            throw DefinitionOverrideException("Try to override definition type '$type' with $definition but has already registered ${definitionsClass[type]}")
        } else {
            definitionsClass[type] = definition
            KoinApplication.log("[Koin] bind type:'$type' ~ $definition")
        }
    }

    private fun saveDefinitionForName(definition: BeanDefinition<*>) {
        definition.name?.let {
            if (definitionsNames[it] != null && !definition.options.override) {
                throw DefinitionOverrideException("Try to override definition name '$it' with $definition but has already registered ${definitionsNames[it]}")
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

    internal fun findAllCreatedAtStartDefinition(): List<BeanDefinition<*>> {
        val effectiveInstances = definitionsClass.values + definitionsNames.values
        return effectiveInstances.distinct().filter { it.options.isCreatedAtStart }
    }

    fun close() {
        definitions.clear()
        definitionsClass.clear()
        definitionsClass.clear()
    }
}

fun HashSet<BeanDefinition<*>>.addDefinition(definition: BeanDefinition<*>) {
    val added = add(definition)
    if (!added && !definition.options.override) {
        throw DefinitionAlreadyExistsException("Already existing definition : $definition & override is not allowed")
    }
}