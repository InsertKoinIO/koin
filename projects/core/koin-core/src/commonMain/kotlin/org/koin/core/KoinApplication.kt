/*
 * Copyright 2017-Present the original author or authors.
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

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.module.KoinApplicationDslMarker
import org.koin.core.module.Module
import org.koin.core.option.KoinOption
import org.koin.core.time.inMs
import org.koin.mp.KoinPlatformTools
import kotlin.time.measureTime

/**
 * Koin Application
 * Help prepare resources for Koin context
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@KoinApplicationDslMarker
class KoinApplication private constructor() {

    val koin = Koin()
    private var allowOverride = true

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
            val duration = measureTime { loadModules(modules) }
            val count = koin.instanceRegistry.size()
            koin.logger.display(Level.INFO, "Started $count definitions in ${duration.inMs} ms")
        } else {
            loadModules(modules)
        }
        return this
    }

    /**
     * Create eager instances (single with createdAtStart)
     */
    fun createEagerInstances() {
        koin.createEagerInstances()
    }

    /**
     * Allow definition override or not, in a global manner
     *
     * @param override
     */
    public fun allowOverride(override: Boolean) {
        allowOverride = override
    }

    private fun loadModules(modules: List<Module>) {
        koin.loadModules(modules, allowOverride = allowOverride, createEagerInstances = false)
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
     * Activate Koin Feature Flag options
     *
     * @see KoinOption for available options
     */
    fun options(vararg optionValue : Pair<KoinOption,Any>): KoinApplication {
        koin.optionRegistry.setValues(optionValue.toMap())
        return this
    }

    /**
     * Set Koin Logger
     * @param logger Koin Logger
     */
    fun logger(logger: Logger): KoinApplication {
        koin.setupLogger(logger)
        return this
    }

    /**
     * Set Koin to use [PrintLogger], by default at [Level.INFO]
     */
    fun printLogger(level: Level = Level.INFO): KoinApplication {
        koin.setupLogger(KoinPlatformTools.defaultLogger(level))
        return this
    }

    fun close() {
        koin.close()
    }

    internal fun unloadModules(modules: List<Module>) {
        koin.unloadModules(modules)
    }

    internal fun unloadModules(module: Module) {
        koin.unloadModules(listOf(module))
    }

    companion object {

        /**
         * Create a new instance of KoinApplication
         */
        fun init(): KoinApplication {
            val app = KoinApplication()
            return app
        }
    }
}
