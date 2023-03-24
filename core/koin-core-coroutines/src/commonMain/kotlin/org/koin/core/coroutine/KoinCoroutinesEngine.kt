/*
 * Copyright 2017-2023 the original author or authors.
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
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.extension.KoinExtension
import org.koin.mp.KoinPlatformCoroutinesTools
import kotlin.coroutines.CoroutineContext

/**
 * Koin Coroutines Engine
 *
 * Help handle coroutines jobs for different purposes
 *
 * @author Arnaud Giulani
 */
@KoinExperimentalAPI
@KoinInternalApi
class KoinCoroutinesEngine : CoroutineScope, KoinExtension {
    private val dispatcher: CoroutineDispatcher = KoinPlatformCoroutinesTools.defaultCoroutineDispatcher()
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = supervisorJob + dispatcher

    internal val startJobs = arrayListOf<Deferred<*>>()

    override lateinit var koin: Koin

    fun <T> launchStartJob(block: suspend CoroutineScope.() -> T){
        startJobs.add(async { block() })
    }

    suspend fun awaitAllStartJobs(){
        koin.logger.debug("await All Start Jobs ...")
        startJobs.map { it.await() }
        startJobs.clear()
    }


    override fun onClose() {
        koin.logger.debug("onClose $this")
        cancel("KoinCoroutinesEngine shutdown")
    }

    companion object {
        const val EXTENSION_NAME = "coroutine-engine"
    }
}

