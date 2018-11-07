package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeId
import org.koin.ext.getFullName
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
        logger.info(
            "registered ${definitions.size} definitions"
        )
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

    internal fun releaseInstanceForScope(scope: Scope) {
        definitions.filter { it.getScopeId() == scope.id }.forEach { it.instance.release(scope) }
    }

    fun close() {
        definitions.clear()
        definitionsNames.clear()
        definitionsClass.clear()
    }
}

