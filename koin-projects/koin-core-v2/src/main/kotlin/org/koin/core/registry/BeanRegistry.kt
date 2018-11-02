package org.koin.core.registry

import org.koin.core.KoinApplication
import org.koin.core.module.Definition
import org.koin.core.module.Module

class BeanRegistry {

    internal val definitions = hashSetOf<Definition<*>>()

    fun loadModules(vararg modulesToLoad: Module) {
        modulesToLoad.forEach { definitions.addAll(it.definitions) }
        KoinApplication.log("[Koin] ${definitions.size} definitions")
    }
}