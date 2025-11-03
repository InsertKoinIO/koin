package org.koin.androidx.compose.navigation3

import androidx.navigation3.runtime.NavEntry
import org.koin.core.annotation.KoinExperimentalAPI

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