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
package org.koin.ktor.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.PluginBuilder
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.util.AttributeKey
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.ModuleDeclaration
import org.koin.mp.KoinPlatformTools

/**
 * @author Arnaud Giuliani
 * @author Vinicius Carvalho
 * @author Victor Alenkov
 * @author Zak Henry
 *
 * Ktor Feature class. Allows Koin Standard Context to start using Ktor default install(<feature>) method.
 *
 */
val Koin =
    createApplicationPlugin(name = "Koin", createConfiguration = { KoinApplication.init() }) {
        val koinApplication = setupKoinApplication()
        KoinPlatformTools.defaultContext().getOrNull()?.let { stopKoin() } // for ktor auto-reload
        startKoin(koinApplication)
        setupMonitoring(koinApplication)
        setupKoinScope(koinApplication)
    }

@OptIn(KoinInternalApi::class)
internal fun PluginBuilder<KoinApplication>.setupKoinApplication(): KoinApplication {
    val koinApplication = pluginConfig
    koinApplication.createEagerInstances()
    //TODO Ktor 3.2
//    koinApplication.koin.resolver.addResolutionExtension(KtorDIExtension(application))
    application.setKoinApplication(koinApplication)
    return koinApplication
}

fun Application.setKoin(koin : Koin) {
    attributes.put(KOIN_ATTRIBUTE_KEY, koin)
}

fun Application.setKoinApplication(koinApplication: KoinApplication) {
    attributes.put(KOIN_ATTRIBUTE_KEY, koinApplication.koin)
}

internal fun PluginBuilder<KoinApplication>.setupMonitoring(koinApplication: KoinApplication) {
    val monitor = application.monitor
    monitor.raise(KoinApplicationStarted, koinApplication)
    monitor.subscribe(ApplicationStopping) {
        monitor.raise(KoinApplicationStopPreparing, koinApplication)
        koinApplication.koin.close()
        monitor.raise(KoinApplicationStopped, koinApplication)
    }
}

internal fun PluginBuilder<KoinApplication>.setupKoinScope(koinApplication: KoinApplication) {
    // Scope Handling
    on(CallSetup) { call ->
        val scopeComponent = RequestScope(koinApplication.koin, call)
        call.attributes.put(KOIN_SCOPE_ATTRIBUTE_KEY, scopeComponent.scope)
    }
    on(ResponseSent) { call ->
        call.attributes[KOIN_SCOPE_ATTRIBUTE_KEY].close()
    }
}

const val KOIN_KEY = "KOIN"
val KOIN_ATTRIBUTE_KEY = AttributeKey<Koin>(KOIN_KEY)

const val KOIN_SCOPE_KEY = "KOIN_SCOPE"
val KOIN_SCOPE_ATTRIBUTE_KEY = AttributeKey<Scope>(KOIN_SCOPE_KEY)

//TODO move both to ext file
/**
 * Scope property to let your resolve dependencies from Request Scope
 */
val ApplicationCall.scope: Scope
    get() = this.attributes.getOrNull(KOIN_SCOPE_ATTRIBUTE_KEY)
        ?: error("Koin Request Scope is not ready")

/**
 * Run extra koin configuration, like modules()
 */
@Deprecated("Use koinModule { } or koinModules() to declare your Koin modules", level = DeprecationLevel.ERROR)
fun Application.koin(configuration: KoinAppDeclaration) = pluginOrNull(Koin)?.let {
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)
} ?: install(Koin, configuration)

/**
 * Get current Koin instance from Ktor Application
 *
 * @return current Koin instance
 */
fun Application.koin() = pluginOrNull(Koin)?.let {
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)
} ?: error("Koin plugin is not installed properly")

/**
 * declare Koin module in current configuration
 *
 * @param moduleDeclaration - module declaration
 */
fun Application.koinModule(moduleDeclaration : ModuleDeclaration) = pluginOrNull(Koin)?.let {
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)?.let { koin ->
        val module = Module().also { moduleDeclaration(it) }
        koin.loadModules(listOf(module))
    } ?: error("Koin plugin is not installed properly")
}

/**
 * declare Koin module in current configuration
 *
 * @param module - list of modules
 */
fun Application.koinModules(vararg module : Module) = pluginOrNull(Koin)?.let {
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)?.let { koin ->
        koin.loadModules(module.toList())
    } ?: error("Koin plugin is not installed properly")
}