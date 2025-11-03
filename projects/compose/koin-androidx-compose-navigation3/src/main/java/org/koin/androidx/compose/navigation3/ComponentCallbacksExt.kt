package org.koin.androidx.compose.navigation3

import android.content.ComponentCallbacks
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import org.koin.android.ext.android.getKoinScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi

/**
 * Creates a lazy [EntryProvider] by collecting all [EntryProviderInstaller] instances from the Koin scope
 * associated with this [ComponentCallbacks].
 *
 * This function aggregates all registered navigation entry providers and creates a composite provider
 * that delegates to all registered installers.
 *
 * @param mode The lazy initialization mode. Defaults to [LazyThreadSafetyMode.SYNCHRONIZED] for thread-safe lazy initialization.
 * @return A lazy delegate that provides the [EntryProvider] when first accessed
 *
 * @see getEntryProvider for eager initialization
 */
@KoinExperimentalAPI
@OptIn(KoinInternalApi::class)
inline fun ComponentCallbacks.entryProvider(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
) : Lazy<EntryProvider> = lazy(mode) { val entries = getKoinScope().getAll<EntryProviderInstaller>()
    val entryProvider: (Any) -> NavEntry<Any> = entryProvider {
        entries.forEach { builder -> this.builder() }
    }
    entryProvider
}

/**
 * Retrieves an [EntryProvider] by collecting all [EntryProviderInstaller] instances from the Koin scope
 * associated with this [ComponentCallbacks].
 *
 * This function aggregates all registered navigation entry providers and creates a composite provider
 * that delegates to all registered installers. Unlike [entryProvider], this function eagerly initializes
 * the provider.
 *
 * @return An [EntryProvider] that combines all registered navigation entries
 *
 * @see entryProvider for lazy initialization
 */
@KoinExperimentalAPI
@OptIn(KoinInternalApi::class)
inline fun ComponentCallbacks.getEntryProvider() : EntryProvider {
    val entries = getKoinScope().getAll<EntryProviderInstaller>()
    val entryProvider: (Any) -> NavEntry<Any> = entryProvider {
        entries.forEach { builder -> this.builder() }
    }
    return entryProvider
}