package org.koin.viewmodel

import androidx.core.bundle.Bundle
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.core.annotation.KoinInternalApi

/**
 * Scope extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Convert current Bundle to CreationExtras
 * @param viewModelStoreOwner
 */
@KoinInternalApi
fun Bundle.toExtras(viewModelStoreOwner: ViewModelStoreOwner): CreationExtras? {
    return if (keySet().isEmpty()) null
    else {
        runCatching {
            MutableCreationExtras().also { extras ->
                extras[DEFAULT_ARGS_KEY] = this
                extras[VIEW_MODEL_STORE_OWNER_KEY] = viewModelStoreOwner
                extras[SAVED_STATE_REGISTRY_OWNER_KEY] = viewModelStoreOwner as SavedStateRegistryOwner
            }
        }.getOrNull()
    }
}

//TODO Replace with CreationExtras API
fun emptyState(): BundleDefinition = { Bundle() }

//TODO Replace with CreationExtras API
typealias BundleDefinition = () -> Bundle