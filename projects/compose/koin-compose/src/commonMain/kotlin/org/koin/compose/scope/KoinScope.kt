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
package org.koin.compose.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.compose.ComposeContextWrapper
import org.koin.compose.LocalKoinScope
import org.koin.compose.getKoin
import org.koin.core.Koin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

/**
 * Create Koin Scope & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeDefinition - lambda to define scope
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
@Composable
fun KoinScope(
    scopeDefinition: Koin.() -> Scope,
    content: @Composable () -> Unit
) {
    val scope = scopeDefinition(getKoin())
    OnKoinScope(scope, content)
}

/**
 * Create Koin Scope from type T & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeID
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
@Composable
inline fun <reified T : Any> KoinScope(
    scopeID: ScopeID,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope<T>(scopeID)
    OnKoinScope(scope, content)
}

/**
 * Create Koin Scope from type T & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeID
 * @param scopeQualifier
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
@Composable
inline fun KoinScope(
    scopeID: ScopeID,
    scopeQualifier: Qualifier,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope(scopeID, scopeQualifier)
    OnKoinScope(scope, content)
}

@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@Composable
@PublishedApi
internal fun OnKoinScope(scope: Scope, content: @Composable () -> Unit) {
    rememberKoinScope(scope)
    CompositionLocalProvider(
        LocalKoinScope provides ComposeContextWrapper(scope),
    ) {
        content()
    }
}