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

import androidx.navigation3.runtime.EntryProviderScope
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Type alias for a function that installs navigation entries into an [EntryProviderScope].
 *
 * An [EntryProviderInstaller] is an extension function on [EntryProviderScope] that allows
 * registering navigation entries (routes and their composable content) within the scope.
 * These installers are collected by Koin and aggregated to build the complete navigation graph.
 *
 * Installers are typically created using the [navigation][org.koin.dsl.navigation3.navigation]
 * DSL function in Koin modules.
 *
 * @see EntryProvider for the aggregated navigation entry provider
 */
@KoinExperimentalAPI
typealias EntryProviderInstaller = EntryProviderScope<Any>.() -> Unit
