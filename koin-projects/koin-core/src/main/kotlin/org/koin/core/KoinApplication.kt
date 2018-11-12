/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core

import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.measureDurationOnly

/**
 * Koin Application
 * Help prepare resources for Koin context usages
 *
 * @author Arnaud Giuliani
 */
class KoinApplication private constructor() {

    val koin = Koin()

    /**
     * Start standalone Koin application
     */
    fun start(): KoinApplication = synchronized(this) {
        val duration = measureDurationOnly {
            saveStandAloneAppInstance()
            createEagerInstances()
        }
        logger.info("started in $duration ms")
        return this
    }

    /**
     * Load definitions from modules
     * @param modules
     */
    fun loadModules(vararg modules: Module): KoinApplication {
        val duration = measureDurationOnly {
            koin.beanRegistry.loadModules(koin, *modules)
        }
        logger.info("modules loaded in $duration ms")
        return this
    }

    /**
     * Load properties from Map
     * @param values
     */
    fun loadProperties(values: Map<String, Any>): KoinApplication {
        koin.propertyRegistry.saveProperties(values)
        return this
    }

    /**
     * Load properties from file
     * @param fileName
     */
    fun loadFileProperties(fileName: String = "/koin.properties"): KoinApplication {
        koin.propertyRegistry.loadPropertiesFromFile(fileName)
        return this
    }

    /**
     * Load properties from environment
     */
    fun loadEnvironmentProperties(): KoinApplication {
        koin.propertyRegistry.loadEnvironmentProperties()
        return this
    }

    /**
     * Set Koin Logger
     * @param level
     * @param log - logger
     */
    @JvmOverloads
    fun useLogger(level: Level = Level.INFO, log: Logger = PrintLogger()): KoinApplication {
        logger = log
        logger.level = level
        return this
    }

    /**
     * Create Single instances Definitions marked as createdAtStart
     */
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

    /**
     * Close all resources from Koin & remove Standalone Koin instance
     */
    fun stop() = synchronized(this) {
        koin.close()
        StandAloneKoinApplication.app = null
        logger.info("stopped")
    }

    companion object {

        var logger: Logger = PrintLogger(Level.ERROR)

        @JvmStatic
        fun create() = KoinApplication()
    }
}