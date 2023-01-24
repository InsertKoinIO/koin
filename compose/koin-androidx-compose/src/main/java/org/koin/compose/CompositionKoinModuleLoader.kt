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

        countInstances()
    }

    override fun onRemembered() {
        // Nothing to do
        koin.logger.debug("$this.onRemembered()")
    }

    override fun onForgotten() {
        koin.logger.debug("$this.onForgotten()")

        unloadModules()
    }

    override fun onAbandoned() {
        koin.logger.debug("$this.onAbandoned()")
        unloadModules()
    }


    private fun unloadModules() {
        koin.unloadModules(modules)
        countInstances()
    }

    private fun countInstances() {
        val count = koin.instanceRegistry.instances.size
        koin.logger.debug("Koin instances: $count")
    }
}