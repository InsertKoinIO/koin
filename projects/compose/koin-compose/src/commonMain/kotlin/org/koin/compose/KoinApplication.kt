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
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform
import org.koin.mp.KoinPlatformTools

/**
 * Current Koin Application context
 */
val LocalKoinApplication: ProvidableCompositionLocal<Koin> = compositionLocalOf {
    getDefaultKoinContext().apply {
        warnNoContext()
    }
}

/**
 * Current Koin Scope
 */
@OptIn(KoinInternalApi::class)
val LocalKoinScope: ProvidableCompositionLocal<Scope> = compositionLocalOf {
    getDefaultKoinContext().apply {
        warnNoContext()
    }.scopeRegistry.rootScope
}

private fun getDefaultKoinContext() = KoinPlatformTools.defaultContext().get()

/**
 * Retrieve the current Koin application from the composition.
 *
 * @author @author jjkester
 */
@Composable
@ReadOnlyComposable
fun getKoin(): Koin = LocalKoinApplication.current

/**
 * Retrieve the current Koin scope from the composition
 *
 * @author @author jjkester
 *
 */
@Composable
@ReadOnlyComposable
fun currentKoinScope(): Scope = LocalKoinScope.current

@OptIn(KoinInternalApi::class)
private fun Koin.warnNoContext() {
    logger.info("[Warning] - No Koin context defined in Compose, fallback to default Koin context." +
        "Use KoinContext(), KoinAndroidContext() or KoinApplication() to setup or create Koin context with Compose and avoid such message.")
}

/**
 * Start a new Koin Application and associate it for Compose context
 * if Koin's Default Context is already set,
 *
 * @param application - Koin Application declaration lambda (like startKoin)
 * @param content - following compose function
 *
 * @throws KoinApplicationAlreadyStartedException
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
@Throws(KoinApplicationAlreadyStartedException::class)
fun KoinApplication(
    application: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    val koinApplication = remember(application) {
        val alreadyExists = KoinPlatformTools.defaultContext().getOrNull() != null
        if (alreadyExists) {
            throw KoinApplicationAlreadyStartedException("Trying to run new Koin Application whereas Koin is already started. Use 'KoinContext()' instead of check for any 'startKoin' usage. ")
        } else {
            startKoin(application)
        }
    }
    CompositionLocalProvider(
        LocalKoinApplication provides koinApplication.koin,
        LocalKoinScope provides koinApplication.koin.scopeRegistry.rootScope,
        content = content
    )
}

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
fun KoinContext(
    context: Koin = KoinPlatform.getKoin(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalKoinApplication provides context,
        LocalKoinScope provides context.scopeRegistry.rootScope,
        content = content
    )
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