package org.koin.androidx.compose.navigation3

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
