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
package org.koin.compose.navigation3

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import org.koin.compose.LocalKoinScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

/**
 * Type alias for a function that provides navigation entries based on a route/destination.
 *
 * An [EntryProvider] takes any route object and returns the corresponding [NavEntry] that
 * contains the composable content for that destination. This enables type-safe navigation
 * with Koin dependency injection.
 *
 * @see EntryProviderInstaller for defining navigation entries in Koin modules
 */
@KoinExperimentalAPI
typealias EntryProvider = (Any) -> NavEntry<Any>

@KoinExperimentalAPI
internal fun Scope.buildEntryProvider() : EntryProvider {
    val entries = getAll<EntryProviderInstaller>()
    val entryProvider: (Any) -> NavEntry<Any> = entryProvider {
        entries.forEach { builder -> this.builder() }
    }
    return entryProvider
}

@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@Composable
fun koinEntryProvider(scope : Scope = LocalKoinScope.current.getValue()) : EntryProvider {
    return scope.buildEntryProvider()
}