package org.koin.core

import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.measureDurationOnly

class KoinApplication {

    val koin = Koin()

    fun start(): KoinApplication = synchronized(this) {
        val duration = measureDurationOnly {
            saveStandAloneAppInstance()
            createEagerInstances()
        }
        logger.info("started in $duration ms")
        return this
    }

    fun loadModules(vararg modulesToLoad: Module): KoinApplication {
        val duration = measureDurationOnly {
            koin.beanRegistry.loadModules(koin, *modulesToLoad)
        }
        logger.info("modules loaded in $duration ms")
        return this
    }

    fun loadProperties(values: Map<String, Any>): KoinApplication {
        koin.properyRegistry.addAll(values)
        return this
    }

    fun loadFileProperties(fileName: String = "/koin.properties"): KoinApplication {
        koin.properyRegistry.loadPropertiesFromFile(fileName)
        return this
    }

    fun loadEnvironmentProperties(): KoinApplication {
        koin.properyRegistry.loadEnvironmentProperties()
        return this
    }

    fun useLogger(level: Level = Level.INFO, log: Logger = PrintLogger()) {
        logger = log
        logger.level = level
    }

    fun createEagerInstances(): KoinApplication {
        val duration = measureDurationOnly {
            koin.createEagerInstances()
        }
        logger.debug("created instances in $duration ms")
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
        logger.info("stopped")
    }

    companion object {

        var logger: Logger = PrintLogger(Level.ERROR)

        fun create() = KoinApplication()
    }
}