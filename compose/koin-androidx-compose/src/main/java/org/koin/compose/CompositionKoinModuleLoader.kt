package org.koin.compose

import androidx.compose.runtime.RememberObserver
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module

@KoinInternalApi
class CompositionKoinModuleLoader(
    val modules : List<Module>,
    val koin : Koin
) : RememberObserver {

    init {
        koin.logger.debug("$this -> load modules")
        koin.loadModules(modules)
    }

    override fun onRemembered() {
        // Nothing to do
    }

    override fun onForgotten() {
        unloadModules()
    }

    override fun onAbandoned() {
        unloadModules()
    }

    private fun unloadModules() {
        koin.logger.debug("$this -> unload modules")
        koin.unloadModules(modules)
    }
}