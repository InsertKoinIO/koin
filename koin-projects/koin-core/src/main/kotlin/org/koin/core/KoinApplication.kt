/*
 * Copyright 2017-2021 the original author or authors.
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

import org.koin.core.annotation.KoinInternal
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.core.time.measureDuration

/**
 * Koin Application
 * Help prepare resources for Koin context
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternal::class)
class KoinApplication private constructor() {

    val koin = Koin()

    /**
     * Load definitions from modules
     * @param modules
     */
    fun modules(modules: Module): KoinApplication {
        return modules(listOf(modules))
    }

    /**
     * Load definitions from modules
     * @param modules
     */
    fun modules(vararg modules: Module): KoinApplication {
        return modules(modules.toList())
    }

    /**
     * Load definitions from modules
     * @param modules
     */
    fun modules(modules: List<Module>): KoinApplication {
        if (koin.logger.isAt(Level.INFO)) {
            val duration = measureDuration {
                loadModules(modules)
            }
            val count = koin.scopeRegistry.size()
            koin.logger.info("loaded $count definitions - $duration ms")
        } else {
            loadModules(modules)
        }
        return this
    }

    private fun loadModules(modules: List<Module>) {
        koin.loadModules(modules)
    }

    /**
     * Load properties from Map
     * @param values
     */
    fun properties(values: Map<String, String>): KoinApplication {
        koin.propertyRegistry.saveProperties(values)
        return this
    }

    /**
     * Load properties from file
     * @param fileName
     */
    fun fileProperties(fileName: String = "/koin.properties"): KoinApplication {
        koin.propertyRegistry.loadPropertiesFromFile(fileName)
        return this
    }

    /**
     * Load properties from environment
     */
    fun environmentProperties(): KoinApplication {
        koin.propertyRegistry.loadEnvironmentProperties()
        return this
    }

    /**
     * Set Koin Logger
     * @param logger - logger
     */
    fun logger(logger: Logger): KoinApplication {
        koin.setupLogger(logger)
        return this
    }

    /**
     * Set Koin to use [PrintLogger], by default at [Level.INFO]
     */
    @JvmOverloads
    fun printLogger(level: Level = Level.INFO) = logger(PrintLogger(level))

    /**
     * Create Single instances Definitions marked as createdAtStart
     */
    fun createEagerInstances(): KoinApplication {
        if (koin.logger.isAt(Level.DEBUG)) {
            val duration = measureDuration {
                koin.createEagerInstances()
            }
            koin.logger.debug("instances started in $duration ms")
        } else {
            koin.createEagerInstances()
        }
        return this
    }

    fun close() {
        koin.close()
    }

    fun unloadModules(module: Module) {
        koin.scopeRegistry.unloadModules(module)
    }

    fun unloadModules(modules: List<Module>) {
        koin.scopeRegistry.unloadModules(modules)
    }


    companion object {

        /**
         * Create a new instance of KoinApplication
         */
        @JvmStatic
        fun init(): KoinApplication {
            return KoinApplication()
        }
    }
}