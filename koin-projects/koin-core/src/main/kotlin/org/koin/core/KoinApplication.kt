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

import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.core.time.measureDurationOnly

/**
 * Koin Application
 * Help prepare resources for Koin context
 *
 * @author Arnaud Giuliani
 */
class KoinApplication private constructor() {

    val koin = Koin()

    internal fun loadDefaults() {
        koin.scopeRegistry.loadDefaultScopes(koin)
    }

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
    fun modules(modules: List<Module>): KoinApplication {
        if (logger.isAt(Level.INFO)) {
            val duration = measureDurationOnly {
                loadModulesAndScopes(modules)
            }
            val count = koin.rootScope.beanRegistry.getAllDefinitions().size + koin.scopeRegistry.getScopeSets().map { it.definitions.size }.sum()
            logger.info("total $count registered definitions")
            logger.info("load modules in $duration ms")
        } else {
            loadModulesAndScopes(modules)
        }
        return this
    }

    private fun loadModulesAndScopes(modules: Iterable<Module>) {
        koin.rootScope.beanRegistry.loadModules(modules)
        koin.scopeRegistry.loadScopes(modules)
    }

    /**
     * Load properties from Map
     * @param values
     */
    fun properties(values: Map<String, Any>): KoinApplication {
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
        KoinApplication.logger = logger
        return this
    }

    /**
     * Set Koin to use [PrintLogger], by default at [Level.INFO]
     */
    @JvmOverloads
    fun printLogger(level: Level = Level.INFO) = this.logger(PrintLogger(level))

    /**
     * Create Single instances Definitions marked as createdAtStart
     */
    fun createEagerInstances(): KoinApplication {
        if (logger.isAt(Level.DEBUG)) {
            val duration = measureDurationOnly {
                koin.createEagerInstances()
            }
            logger.debug("instances started in $duration ms")
        } else {
            koin.createEagerInstances()
        }
        return this
    }

    /**
     * Close all resources from Koin & remove Standalone Koin instance
     */
    fun close() = synchronized(this) {
        koin.close()
        if (logger.isAt(Level.INFO)) {
            logger.info("stopped")
        }
    }

    fun unloadModules(vararg modules: Module): KoinApplication {
        return unloadModules(modules.toList())
    }

    fun unloadModules(modules: List<Module>): KoinApplication {
        koin.rootScope.beanRegistry.unloadModules(modules)
        koin.scopeRegistry.unloadScopedDefinitions(modules)
        return this
    }

    companion object {

        var logger: Logger = EmptyLogger()

        /**
         * Create a new instance of KoinApplication
         */
        @JvmStatic
        fun create(): KoinApplication {
            val app = KoinApplication()
            app.loadDefaults()
            return app
        }
    }
}