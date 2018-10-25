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
package org.koin.standalone

import org.koin.core.Koin
import org.koin.core.KoinProperties
import org.koin.core.instance.ModuleCallBack
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.ScopeCallback
import org.koin.core.time.measureDuration
import org.koin.dsl.module.Module
import org.koin.error.AlreadyStartedException
import org.koin.log.Logger
import org.koin.log.PrintLogger


/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {

    /**
     * Koin ModuleDefinition
     */
    private var koin: Koin? = null

    /**
     * Koin starter function to load modules and extraProperties
     * Throw AlreadyStartedException if already started
     * @param list : Modules
     * @param properties : KoinProperties
     * @param logger - Koin logger
     */
    fun startKoin(
        list: List<Module>,
        properties: KoinProperties = KoinProperties(),
        logger: Logger = PrintLogger()
    ): Koin {
        if (koin != null) {
            throw AlreadyStartedException("Koin is already started. Run startKoin only once or use loadKoinModules")
        }
        Koin.logger = logger
        val duration = measureDuration {
            createFullKoin(properties, list)
        }
        Koin.logger.info("[Koin] started in $duration ms")
        return getKoin()
    }

    /**
     * Get Koin instance
     */
    fun getKoin(): Koin = koin ?: error("StandAloneContext Koin instance is null")

    /**
     * Setup new Koin instance
     */
    fun setup(newKoinInstance: Koin) {
        koin = newKoinInstance
    }


    private fun createFullKoin(
        properties: KoinProperties,
        list: List<Module>
    ) {
        koin = Koin.create()
        koin?.apply {
            loadProperties(properties)
            loadKoinModules(list)
            createEagerInstances(emptyParameterDefinition())
        }
    }

    /**
     * Close actual Koin context
     */
    @Deprecated("Renamed, please use stopKoin() instead.")
    fun closeKoin() = stopKoin()

    /**
     * Close actual Koin context
     * - drop akk instances & definitions
     */
    fun stopKoin() = synchronized(this) {
        koin?.close()
        koin = null
    }

    /**
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(vararg modules: Module): Koin {
        return getOrCreateMinimalKoin(modules.toList())
    }

    /**
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(modules: List<Module>): Koin = loadKoinModules(*modules.toTypedArray())

    private fun getOrCreateMinimalKoin(
        list: List<Module>
    ): Koin = synchronized(this) {
        if (koin == null) {
            koin = Koin.create()
        }
        koin?.apply {
            loadModules(list)
        }
        return getKoin()
    }

    /**
     * Register ScopeCallback - being notified on Scope closing
     * @see ScopeCallback - ScopeCallback
     */
    fun registerScopeCallback(callback: ScopeCallback) {
        koin?.registerScopeCallback(callback)
    }

    /**
     * Register ModuleCallBack - being notified on Path release
     * @see ScopeCallback - ModuleCallBack
     *
     * Deprecared - Use the Scope API
     */
    @Deprecated("Please use the Scope API instead.")
    fun registerModuleCallBack(callback: ModuleCallBack) {
        koin?.registerModuleCallBack(callback)
    }
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext