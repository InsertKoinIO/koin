package org.koin.core

import org.koin.core.error.KoinAlreadyStartedException
import org.koin.core.module.Module
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.logDuration


typealias KoinAppDeclaration = KoinApplication.() -> Unit

class KoinApplication {

    val koin = Koin()

    init {
        logDuration("[Koin] create") { }
    }

    fun loadModules(vararg modulesToLoad: Module) {
        logDuration("[Koin] load modules") {
            koin.beanRegistry.loadModules(*modulesToLoad)
        }
    }

    fun start(): KoinApplication {
        logDuration("[Koin] standalone start") {
            if (StandAloneKoinApplication.app != null) {
                throw KoinAlreadyStartedException("KoinApplication is already started")
            }
            StandAloneKoinApplication.app = this
        }
        return this
    }

    fun stop() {
        logDuration("[Koin] standalone stop") {
            StandAloneKoinApplication.app = null
        }
    }

    companion object {
        fun log(msg: String) = println(msg)
        fun create() = KoinApplication()
    }
}