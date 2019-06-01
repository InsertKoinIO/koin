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
package org.koin.core.context

import org.koin.core.KoinApplication
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Global context - current Koin Application available globally
 *
 * Support to help inject automatically instances once KoinApp has been started
 *
 */
object GlobalContext {

    internal var app: KoinApplication? = null

    /**
     * StandAlone Koin App instance
     */
    @JvmStatic
    fun get(): KoinApplication = app ?: error("KoinApplication has not been started")

    /**
     * StandAlone Koin App instance
     */
    @JvmStatic
    fun getOrNull(): KoinApplication? = app

    /**
     * Start a Koin Application as StandAlone
     */
    @JvmStatic
    fun start(koinApplication: KoinApplication) {
        if (app != null) {
            throw KoinAppAlreadyStartedException("A Koin Application has already been started")
        }
        app = koinApplication
    }

    /**
     * Stop current StandAlone Koin application
     */
    @JvmStatic
    fun stop() = synchronized(this) {
        app?.close()
        app = null
    }
}

/**
 * Start a Koin Application as StandAlone
 */
fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication {
    val koinApplication = KoinApplication.create()
    GlobalContext.start(koinApplication)
    appDeclaration(koinApplication)
    koinApplication.createEagerInstances()
    return koinApplication
}

/**
 * Stop current StandAlone Koin application
 */
fun stopKoin() = GlobalContext.stop()

/**
 * load Koin modules in global Koin context
 */
fun loadKoinModules(module: Module) {
    GlobalContext.get().modules(listOf(module))
}


fun loadKoinModules(modules: List<Module>) {
    GlobalContext.get().modules(modules)
}

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(module: Module) {
    GlobalContext.get().unloadModules(listOf(module))
}

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(modules: List<Module>) {
    GlobalContext.get().unloadModules(modules)
}
