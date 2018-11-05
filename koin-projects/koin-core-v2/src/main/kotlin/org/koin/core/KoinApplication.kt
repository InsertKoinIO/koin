package org.koin.core

import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.logDuration

class KoinApplication {

    val koin = Koin()

    init {
        KoinApplication.log("[Koin] create")
    }

    fun loadModules(vararg modulesToLoad: Module) {
        KoinApplication.log("[Koin] load modules")
        logDuration("[Koin] modules loaded") {
            koin.beanRegistry.loadModules(koin, *modulesToLoad)
        }
    }

    fun start(): KoinApplication = synchronized(this) {
        logDuration("[Koin] started") {
            saveStandAloneAppInstance()
            createEagerInstances()
        }
        return this
    }

    fun createEagerInstances(): KoinApplication {
        KoinApplication.log("[Koin] creating instances at start ...")
        logDuration("[Koin] created instances at start") {
            koin.createEagerInstances()
        }
        return this
    }

    private fun saveStandAloneAppInstance() {
        if (StandAloneKoinApplication.app != null) {
            throw KoinAppAlreadyStartedException("KoinApplication is already started")
        }
        StandAloneKoinApplication.app = this
    }

    fun stop() = synchronized(this) {
        koin.close()
        StandAloneKoinApplication.app = null
        KoinApplication.log("[Koin] stopped")
    }

    companion object {
        fun log(msg: String) = println(msg)
        fun create() = KoinApplication()
    }
}