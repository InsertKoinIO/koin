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

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

internal fun globalContextByMemoryModel(): KoinContext = MutableGlobalContext()

/**
 * Mutable global context for the new memory model. Very similar to how the JVM global context works.
 */
internal class MutableGlobalContext : KoinContext {
    private val lock = Lock()
    private var _koin: Koin? = null
    private var _koinApplication: KoinApplication? = null

    override fun get(): Koin = _koin ?: error("KoinApplication has not been started")

    override fun getOrNull(): Koin? = _koin

    fun getKoinApplicationOrNull(): KoinApplication? = _koinApplication

    private fun register(koinApplication: KoinApplication) {
        if (_koin != null) {
            throw KoinApplicationAlreadyStartedException("A Koin Application has already been started")
        }
        _koinApplication = koinApplication
        _koin = koinApplication.koin
    }

    override fun stopKoin() = lock.withLock {
        _koin?.close()
        _koin = null
    }

    override fun startKoin(koinApplication: KoinApplication): KoinApplication = lock.withLock {
        register(koinApplication)
        koinApplication.createEagerInstances()
        koinApplication
    }

    override fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = lock.withLock {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()
        koinApplication
    }

    override fun loadKoinModules(module: Module, createEagerInstances : Boolean) = lock.withLock {
        get().loadModules(listOf(module), createEagerInstances = createEagerInstances)
    }

    override fun loadKoinModules(modules: List<Module>, createEagerInstances : Boolean) = lock.withLock {
        get().loadModules(modules, createEagerInstances = createEagerInstances)
    }

    override fun unloadKoinModules(module: Module) = lock.withLock {
        get().unloadModules(listOf(module))
    }

    override fun unloadKoinModules(modules: List<Module>) = lock.withLock {
        get().unloadModules(modules)
    }
}
