package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.AlreadyExistingDefinition
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

    private fun saveDefinition(definition: BeanDefinition<*>) {
        val added = definitions.add(definition)
        if (!added) {
            throw AlreadyExistingDefinition("Already existing definition : $definition")
        } else {
            definition.name?.let {
                definitionsNames[it] = definition
            }
            definitionsClass[definition.primaryType] = definition

            KoinApplication.log("[Koin] definition ~ $definition")
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