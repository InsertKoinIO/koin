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
package org.koin.core.context

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools
import org.koin.mp.native.createdGuardedValue

/**
 * Global context - current Koin Application available globally
 *
 * Support to help inject automatically instances once KoinApp has been started
 *
 * @author Arnaud Giuliani
 */
object GlobalContext : KoinContext {

    data class KoinInstanceHolder(var koin: Koin? = null)

    private val contextHolder = createdGuardedValue(KoinInstanceHolder(null))

    override fun getOrNull(): Koin? = contextHolder.get().koin
    override fun get(): Koin = getOrNull() ?: error("KoinApplication has not been started")

    private fun register(koinApplication: KoinApplication) {
        if (getOrNull() != null) {
            throw KoinAppAlreadyStartedException("A Koin Application has already been started")
        }
        contextHolder.get().koin = koinApplication.koin
    }

    override fun stopKoin() = KoinPlatformTools.synchronized(this) {
        contextHolder.get().koin?.close()
        contextHolder.get().koin = null
    }


    override fun startKoin(koinApplication: KoinApplication): KoinApplication = KoinPlatformTools.synchronized(this) {
        register(koinApplication)
        koinApplication
    }

    override fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = KoinPlatformTools.synchronized(this) {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication
    }


    override fun loadKoinModules(module: Module) = KoinPlatformTools.synchronized(this) {
        get().loadModules(listOf(module))
    }

    override fun loadKoinModules(modules: List<Module>) = KoinPlatformTools.synchronized(this) {
        get().loadModules(modules)
    }

    override fun unloadKoinModules(module: Module) = KoinPlatformTools.synchronized(this) {
        get().unloadModules(listOf(module))
    }

    override fun unloadKoinModules(modules: List<Module>) = KoinPlatformTools.synchronized(this) {
        get().unloadModules(modules)
    }
}
