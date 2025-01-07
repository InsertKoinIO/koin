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
package org.koin.core.coroutine

import kotlinx.coroutines.*
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.extension.KoinExtension
import org.koin.core.logger.Logger
import org.koin.mp.KoinPlatformCoroutinesTools
import kotlin.coroutines.CoroutineContext

/**
 * Koin Coroutines Engine
 *
 * Help handle coroutines jobs for different purposes
 *
 * @author Arnaud Giuliani
 */
@KoinInternalApi
class KoinCoroutinesEngine(coroutineDispatcher: CoroutineDispatcher? = null) : CoroutineScope, KoinExtension {
    private val dispatcher: CoroutineDispatcher = coroutineDispatcher ?: KoinPlatformCoroutinesTools.defaultCoroutineDispatcher()
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = supervisorJob + dispatcher

    internal val startJobs = arrayListOf<Deferred<*>>()

    private var _koin: Koin? = null
    private fun getKoin() : Koin = _koin ?: error("No Koin instance is registered for plugin $this")
    private fun getLogger() : Logger = getKoin().logger

    override fun onRegister(koin: Koin) {
        _koin = koin
        koin.logger.debug("$TAG - init ($dispatcher)")
    }

    fun <T> launchStartJob(block: suspend CoroutineScope.() -> T) {
        startJobs.add(async { block() })
    }

    suspend fun awaitAllStartJobs() {
        getLogger().debug("$TAG - await All Start Jobs ...")
        startJobs.awaitAll()
        startJobs.clear()
    }

    override fun onClose() {
        getLogger().debug("$TAG - onClose $this")
        cancel("KoinCoroutinesEngine shutdown")
    }

    companion object {
        const val TAG = "[CoroutinesEngine]"
        const val EXTENSION_NAME = "coroutine-engine"
    }
}
