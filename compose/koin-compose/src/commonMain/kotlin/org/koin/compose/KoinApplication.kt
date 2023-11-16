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

@file:OptIn(KoinInternalApi::class)

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import org.koin.compose.error.UnknownKoinContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.error.ApplicationAlreadyStartedException
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

/**
 * Current Koin Application context
 */
val LocalKoinApplication: ProvidableCompositionLocal<Koin> = compositionLocalOf {
    throw UnknownKoinContext()
}

/**
 * Current Koin Scope
 */
val LocalKoinScope: ProvidableCompositionLocal<Scope> = compositionLocalOf {
    throw UnknownKoinContext()
}

private fun getKoinContext() = KoinPlatformTools.defaultContext().get()

/**
 * Retrieve the current Koin application from the composition.
 *
 * @author @author jjkester
 */
@OptIn(InternalComposeApi::class)
@Composable
fun getKoin(): Koin = currentComposer.run {
    remember {
        try {
            consume(LocalKoinApplication)
        } catch (_: UnknownKoinContext) {
            val ctx = getKoinContext()
            ctx.warnNoContext()
            ctx
        }
    }
}

/**
 * Retrieve the current Koin scope from the composition
 *
 * @author @author jjkester
 *
 */
@OptIn(InternalComposeApi::class)
@Composable
fun getKoinScope(): Scope = currentComposer.run {
    remember {
        try {
            consume(LocalKoinScope)
        } catch (_: UnknownKoinContext) {
            val ctx = getKoinContext()
            ctx.warnNoContext()
            getKoinContext().scopeRegistry.rootScope
        }
    }
}

private fun Koin.warnNoContext() {
    logger.debug("[Warning] - No Compose Koin context setup, taking default. Use KoinContext(), KoinAndroidContext() or KoinApplication() function to setup or create Koin context and avoid such message.")
}

/**
 * Start a new Koin Application and associate it for Compose context
 * if Koin's Default Context is already set,
 *
 * @param application - Koin Application declaration lambda (like startKoin)
 * @param content - following compose function
 *
 * @throws ApplicationAlreadyStartedException
 * @author Arnaud Giuliani
 */
@Composable
@Throws(ApplicationAlreadyStartedException::class)
fun KoinApplication(
    application: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    val koinApplication = remember(application) {
        val alreadyExists = KoinPlatformTools.defaultContext().getOrNull() != null
        if (alreadyExists){
            throw ApplicationAlreadyStartedException("Trying to run new Koin Application whereas Koin is already started. Use 'KoinContext()' instead of check for any 'startKoin' usage. ")
        }
        else {
            startKoin(application)
        }
    }
    CompositionLocalProvider(
        LocalKoinApplication provides koinApplication.koin,
        LocalKoinScope provides koinApplication.koin.scopeRegistry.rootScope
    ) {
        content()
    }
}

/**
 * Use Compose with existing Koin context, by default 'KoinPlatformTools.defaultContext()'
 *
 * @see KoinPlatformTools.defaultContext()
 * @param content - following compose function
 *
 * @author Arnaud Giuliani
 */
@Composable
fun KoinContext(
    context: Koin = KoinPlatformTools.defaultContext().get(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalKoinApplication provides context,
        LocalKoinScope provides context.scopeRegistry.rootScope
    ) {
        content()
    }
}

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
@Composable
fun KoinIsolatedContext(
    context: KoinApplication,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalKoinApplication provides context.koin,
        LocalKoinScope provides context.koin.scopeRegistry.rootScope
    ) {
        content()
    }
}