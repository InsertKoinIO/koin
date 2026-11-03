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
@file:OptIn(KoinInternalApi::class)

package org.koin.ktor.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.application.PluginBuilder
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.util.AttributeKey
import io.ktor.util.cio.ChannelIOException
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.isActive
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinKtorApplication
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
    createApplicationPlugin(
        name = "Koin",
        createConfiguration = { KoinKtorApplication.init() })
    {
        val koinApplication = setupKoinApplication()
        KoinPlatformTools.defaultContext().getOrNull()?.let { stopKoin() } // for ktor auto-reload
        startKoin(koinApplication)

        koinApplication.onPostStart()

        setupMonitoring(koinApplication)
        setupKoinScope(koinApplication)
    }

internal fun PluginBuilder<KoinKtorApplication>.setupKoinApplication(): KoinKtorApplication {
    val koinApplication = pluginConfig
    koinApplication.createEagerInstances()

    koinApplication.ktorApplication = application
    application.setKoinApplication(koinApplication)

    return koinApplication
}

fun Application.setKoin(koin : Koin) {
    attributes.put(KOIN_ATTRIBUTE_KEY, koin)
}

fun Application.setKoinApplication(koinApplication: KoinApplication) {
    attributes.put(KOIN_ATTRIBUTE_KEY, koinApplication.koin)
}

internal fun PluginBuilder<KoinKtorApplication>.setupMonitoring(koinApplication: KoinKtorApplication) {
    val monitor = application.monitor
    monitor.raise(KoinApplicationStarted, koinApplication)
    monitor.subscribe(ApplicationStopping) {
        monitor.raise(KoinApplicationStopPreparing, koinApplication)
        koinApplication.koin.close()
        monitor.raise(KoinApplicationStopped, koinApplication)
    }
}

internal fun PluginBuilder<KoinKtorApplication>.setupKoinScope(koinApplication: KoinKtorApplication) {
    // Scope Handling
    on(CallSetup) { call ->
        val scopeComponent = RequestScope(koinApplication.koin, call)
        call.attributes.put(KOIN_SCOPE_ATTRIBUTE_KEY, scopeComponent.scope)
    }
    // Every answered call closes here, the engine's own error response included.
    on(ResponseSent) { call ->
        call.closeRequestScope()
    }
    closeRequestScopeOfUnansweredCalls()
    closeRequestScopeWhenErrorResponseFails()
}

// Calls the engine will not answer never reach ResponseSent: the response is already committed (a stream failing
// mid-body, a response hook failing after Koin's #2463), the call is cancelled (#2472), or the failure is a channel
// I/O error the engine only logs (DefaultEnginePipeline). Closing once the whole application pipeline has unwound
// keeps the scope open for the handler's own catch blocks and every CallFailed handler, StatusPages included.
private fun PluginBuilder<KoinKtorApplication>.closeRequestScopeOfUnansweredCalls() {
    application.insertPhaseBefore(application.items.first(), RequestScopeLifecyclePhase)
    application.intercept(RequestScopeLifecyclePhase) {
        try {
            proceed()
        } catch (failure: Throwable) {
            call.attributes.put(RequestPipelineExited, Unit)
            val unanswered = call.response.isCommitted || !coroutineContext.isActive || failure is ChannelIOException
            if (unanswered) call.closeRequestScope(failure)
            throw failure
        }
        call.attributes.put(RequestPipelineExited, Unit)
        // A handler that caught its own failed send exits normally with a committed, never-sent response.
        if (call.response.isCommitted) call.closeRequestScope()
    }
}

// The engine's own error response, sent after the application pipeline exited, can fail after commit too.
private fun PluginBuilder<KoinKtorApplication>.closeRequestScopeWhenErrorResponseFails() {
    val sendPipeline = application.sendPipeline
    sendPipeline.insertPhaseBefore(sendPipeline.items.first(), RequestScopeSendGuardPhase)
    sendPipeline.intercept(RequestScopeSendGuardPhase) {
        try {
            proceed()
        } catch (failure: Throwable) {
            if (call.response.isCommitted && call.attributes.contains(RequestPipelineExited)) call.closeRequestScope(failure)
            throw failure
        }
    }
}

private val RequestScopeLifecyclePhase = PipelinePhase("KoinRequestScopeLifecycle")
private val RequestScopeSendGuardPhase = PipelinePhase("KoinRequestScopeSendGuard")
private val RequestPipelineExited = AttributeKey<Unit>("KoinRequestPipelineExited")

private fun ApplicationCall.closeRequestScope(inFlight: Throwable? = null) {
    val scope = attributes.getOrNull(KOIN_SCOPE_ATTRIBUTE_KEY) ?: return
    if (scope.closed) return
    try {
        scope.close()
    } catch (closeFailure: Throwable) {
        // Never mask the failure that is already unwinding the call.
        if (inFlight == null) throw closeFailure
        inFlight.addSuppressed(closeFailure)
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