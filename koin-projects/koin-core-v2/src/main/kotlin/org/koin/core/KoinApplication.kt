package org.koin.core

import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.logDuration

class KoinApplication {

    val koin = Koin()

    fun start(): KoinApplication = synchronized(this) {
        logDuration("[Koin] started", Level.INFO) {
            saveStandAloneAppInstance()
            createEagerInstances()
        }
        return this
    }

    fun loadModules(vararg modulesToLoad: Module) {
        KoinApplication.logger.info { "[Koin] load modules" }
        logDuration("[Koin] modules loaded", Level.INFO) {
            koin.beanRegistry.loadModules(koin, *modulesToLoad)
        }
    }

    fun useLogger(level: Level = Level.INFO, logger: Logger = PrintLogger()) {
        KoinApplication.logger = logger
        KoinApplication.logger.level = level
    }

    fun createEagerInstances(): KoinApplication {
        logger.debug { "[Koin] creating instances at start ..." }
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
        logger.info { "[Koin] stopped" }
    }

    companion object {

        var logger: Logger = PrintLogger(Level.ERROR)

        fun create() = KoinApplication()
    }
}