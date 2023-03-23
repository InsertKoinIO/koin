/*
 * Copyright 2017-2019 the original author or authors.
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
package org.koin.ktor.plugin

import io.ktor.server.application.*
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * @author Arnaud Giuliani
 * @author Vinicius Carvalho
 * @author Victor Alenkov
 *
 * Ktor Feature class. Allows Koin Context to start using Ktor default install(<feature>) method.
 *
 */

// Plugin
val Koin = createApplicationPlugin(name = "Koin", createConfiguration = { KoinApplication.init() }) {
    val monitor = environment?.monitor
    val koinApplication = startKoin(pluginConfig)
    monitor?.raise(KoinApplicationStarted, koinApplication)

    monitor?.subscribe(ApplicationStopping) {
        monitor.raise(KoinApplicationStopPreparing, koinApplication)
        stopKoin()
        monitor.raise(KoinApplicationStopped, koinApplication)
    }
}

fun Application.koin(configuration: KoinAppDeclaration) = pluginOrNull(Koin)?.let {
    GlobalContext.getKoinApplicationOrNull()?.apply(configuration)
} ?: install(Koin, configuration)