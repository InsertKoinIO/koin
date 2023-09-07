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
package org.koin.core.context

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Hold Current Koin context
 *
 * @author Arnaud Giuliani
 */
interface KoinContext {

    /**
     * Get Koin instance
     */
    fun get(): Koin

    /**
     * Get Koin instance or null
     */
    fun getOrNull(): Koin?

    /**
     * Stop current Koin instance
     */
    fun stopKoin()

    /**
     * Start a Koin Application as StandAlone
     */
    fun startKoin(koinApplication: KoinApplication): KoinApplication

    /**
     * Start a Koin Application as StandAlone
     */
    fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication

    /**
     * load Koin module in global Koin context
     */
    fun loadKoinModules(module: Module, createEagerInstances: Boolean = false)

    /**
     * load Koin modules in global Koin context
     */
    fun loadKoinModules(modules: List<Module>, createEagerInstances: Boolean = false)

    /**
     * unload Koin module from global Koin context
     */
    fun unloadKoinModules(module: Module)

    /**
     * unload Koin modules from global Koin context
     */
    fun unloadKoinModules(modules: List<Module>)
}
