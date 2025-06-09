/*
 * Copyright 2017-present the original author or authors.
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

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentComposer
import org.koin.compose.application.rememberKoinApplication
import org.koin.compose.application.rememberKoinMPApplication
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.ClosedScopeException
import org.koin.core.logger.Level
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatform
import org.koin.mp.KoinPlatformTools

/**
 * Current Koin Application context, as default with Default Koin context
 */
val LocalKoinApplication: ProvidableCompositionLocal<Koin> = compositionLocalOf { getDefaultKoinContext() }

/**
 * Current Koin Scope, as default with Default Koin context root scope
 */
@OptIn(KoinInternalApi::class)
val LocalKoinScope: ProvidableCompositionLocal<Scope> = compositionLocalOf { getDefaultKoinContext().scopeRegistry.rootScope }

private fun getDefaultKoinContext() = KoinPlatform.getKoin()

/**
 * Retrieve the current Koin application from the composition.
 *
 * @author @author jjkester
 */
@OptIn(InternalComposeApi::class, KoinInternalApi::class)
@Composable
fun getKoin(): Koin = currentComposer.run {
    try {
        consume(LocalKoinApplication)
    } catch (e: Exception) {
        KoinPlatform.getKoinOrNull()?.let {
            it.logger.debug("Error while accessing Koin context. Fallback on default context ...")
            it
        } ?: error("Can't get Koin context due to error:$e")
    }
}

/**
 * Retrieve the current Koin scope from the composition
 *
 * @author @author jjkester
 *
 */
@OptIn(InternalComposeApi::class, KoinInternalApi::class)
@Composable
fun currentKoinScope(): Scope = currentComposer.run {
    try {
        consume(LocalKoinScope)
    } catch (e: ClosedScopeException) {
        KoinPlatform.getKoinOrNull()?.let {
            it.logger.debug("Error while accessing Koin scope. Fallback on default root scope...")
            it.scopeRegistry.rootScope
        } ?: error("Can't get Koin scope due to error:$e")
    }
}

/**
 * Start a new Koin Application context and setup Compose context
 * if Koin's Default Context is already set, throw an error
 *
 * @param application - Koin Application declaration lambda
 * @param content - following compose function
 *
 * @throws org.koin.core.error.KoinApplicationAlreadyStartedException
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
fun KoinApplication(
    application: KoinAppDeclaration, //Better to directly use KoinConfiguration class
    content: @Composable () -> Unit
) {
    val koin = rememberKoinApplication(application)
    CompositionLocalProvider(
        LocalKoinApplication provides koin,
        LocalKoinScope provides koin.scopeRegistry.rootScope,
        content = content
    )
}

/**
 * Start a new Koin Application context, configure default context binding (android) & logger, setup Compose context
 * if Koin's Default Context is already set, throw an error
 *
 * Call composeMultiplatformConfiguration to help prepare/anticipate context setup, and avoid to have different configuration in KMP app
 * this function takes care to setup Android context (androidContext, androidLogger) for you
 * @see composeMultiplatformConfiguration()
 *
 * @param config - Koin Application Configuration (use koinConfiguration { } to declare your Koin application)
 * @see KoinConfiguration
 *
 * @param logLevel - KMP active logger (androidLogger or printLogger)
 * @param content - following compose function
 *
 * @throws org.koin.core.error.KoinApplicationAlreadyStartedException
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
@KoinExperimentalAPI
fun KoinMultiplatformApplication(
    config: KoinConfiguration,
    logLevel : Level = Level.INFO,
    content: @Composable () -> Unit
) {
    val koin = rememberKoinMPApplication(config,logLevel)
    CompositionLocalProvider(
        LocalKoinApplication provides koin,
        LocalKoinScope provides koin.scopeRegistry.rootScope,
        content = content
    )
}

/**
 * Handle Multiplatform Config & Logger level
 * - Help handle automatically Android Logger Anticipate Android context injection, to having to setup androidContext() and androidLogger
 */
@Composable
@PublishedApi
@KoinInternalApi
internal expect fun composeMultiplatformConfiguration(loggerLevel : Level = Level.INFO, config : KoinConfiguration) : KoinConfiguration

/**
 * Use Compose with current existing Koin context, by default 'KoinPlatform.getKoin()'
 *
 * @see KoinPlatformTools.defaultContext()
 * @param content - following compose function
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
@Deprecated("KoinContext is not needed anymore. This can be removed. Compose Koin context is setup with StartKoin()")
fun KoinContext(
    context: Koin = retrieveDefaultInstance(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalKoinApplication provides context,
        LocalKoinScope provides context.scopeRegistry.rootScope,
        content = content
    )
}

@Composable
@Deprecated("KoinContext is not needed anymore. This can be removed. Compose Koin context is setup with StartKoin()")
internal expect fun retrieveDefaultInstance() : Koin

/**
 * Provides Koin Isolated context to be setup into LocalKoinApplication & LocalKoinScope via CompositionLocalProvider,
 * to be used by child Composable.
 *
 * This allows to use an isolated context, directly in all current Composable API
 *
 * Koin isolated context has to created with koinApplication() function, storing the instance in a static field
 *
 * @param context - Koin isolated context
 * @param content - child Composable
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
fun KoinIsolatedContext(
    context: KoinApplication,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalKoinApplication provides context.koin,
        LocalKoinScope provides context.koin.scopeRegistry.rootScope,
        content = content
    )
}

/**
 * Composable Function to run a local Koin application and to help run Compose preview
 * This function is lighter than KoinApplication, and allow parallel recomposition in Android Studio
 *
 * @param application - Koin application config
 * @param content
 */
@OptIn(KoinInternalApi::class)
@Composable
fun KoinApplicationPreview(
    application: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    val context = koinApplication(application)
    CompositionLocalProvider(
        LocalKoinApplication provides context.koin,
        LocalKoinScope provides context.koin.scopeRegistry.rootScope,
        content = content
    )
}