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

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatformTools

/**
 * Current Koin Application context
 */
val LocalKoinApplication = compositionLocalOf<Koin> { throw UnknownKoinContext() }

/**
 * Current Koin Scope
 */
val LocalKoinScope = compositionLocalOf<Scope> { throw UnknownKoinContext() }

/**
 * Marker exception indicating that no Koin context is present in the composition
 *
 * @author Jan-Jelle Kester
 */
internal class UnknownKoinContext : RuntimeException("No Koin context has been provided")

private fun getKoinContext() = KoinPlatformTools.defaultContext().get()

/**
 * Retrieve the current Koin application from the composition.
 */
@OptIn(InternalComposeApi::class)
@Composable
fun getKoin(): Koin = currentComposer.run {
    remember {
        try {
            consume(LocalKoinApplication)
        } catch (_: UnknownKoinContext) {
            getKoinContext()
        }
    }
}

/**
 * Retrieve the current Koin scope from the composition
 */
@OptIn(InternalComposeApi::class)
@Composable
fun getKoinScope(): Scope = currentComposer.run {
    remember {
        try {
            consume(LocalKoinScope)
        } catch (_: UnknownKoinContext) {
            getKoinContext().scopeRegistry.rootScope
        }
    }
}

/**
 * Start Koin Application from Compose
 *
 * @param application - Koin Application declaration lambda (like startKoin)
 * @param content - following compose function
 *
 * @author Arnaud Giuliani
 */
@Composable
fun KoinApplication(
    application: KoinAppDeclaration,
    content: @Composable () -> Unit
) {
    val koinApplication = koinApplication(application)
    CompositionLocalProvider(
        LocalKoinApplication provides koinApplication.koin,
        LocalKoinScope provides koinApplication.koin.scopeRegistry.rootScope
    ) {
        content()
    }
}

/**
 * Start Koin Application from Compose
 *
 * @param moduleList - list of Modules to run within Koin Application
 * @param content - following compose function
 *
 * @author Arnaud Giuliani
 */
@Composable
fun KoinApplication(
    moduleList: () -> List<Module>,
    content: @Composable () -> Unit
) {
    val koinApplication = koinApplication { modules(moduleList()) }
    CompositionLocalProvider(
        LocalKoinApplication provides koinApplication.koin,
        LocalKoinScope provides koinApplication.koin.scopeRegistry.rootScope
    ) {
        content()
    }
}
