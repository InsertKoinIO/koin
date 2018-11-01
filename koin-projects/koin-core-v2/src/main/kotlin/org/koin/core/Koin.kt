package org.koin.core

import org.koin.core.standalone.StandAloneContext
import org.koin.core.time.logDuration


typealias KoinAppDeclaration = Koin.() -> Unit

class Koin {

    init {
        logDuration("[Koin] create") { }
    }

    internal val definitions = arrayListOf<Definition<*>>()

    fun loadModules(vararg modulesToLoad: Module) {
        logDuration("[Koin] load modules") {
            modulesToLoad.forEach { definitions.addAll(it.definitions) }
        }
        log("[Koin] definitions - ${definitions.size}")
    }

    fun start(): Koin {
        logDuration("[Koin] standalone start") {
            StandAloneContext.koin = this
        }
        return this
    }

    fun stop() {
        logDuration("[Koin] standalone stop") {
            StandAloneContext.koin = null
        }
    }

    companion object {
        fun log(msg: String) = println(msg)
        fun create() = Koin()
    }
}