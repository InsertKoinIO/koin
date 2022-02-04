package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner

typealias ViewModelOwnerDefinition = () -> ViewModelOwner

class ViewModelOwner(
        val store: ViewModelStore,
        val stateRegistry: SavedStateRegistryOwner? = null,
        val defaultArgs: Bundle? = null
) {
    companion object {
        fun from(storeOwner: ViewModelStoreOwner, stateRegistry: SavedStateRegistryOwner? = null) = ViewModelOwner(
            storeOwner.viewModelStore,
            stateRegistry,
            if (storeOwner is NavBackStackEntry) storeOwner.arguments else null
        )

        fun from(storeOwner: ViewModelStoreOwner) = ViewModelOwner(
            storeOwner.viewModelStore,
            defaultArgs = if (storeOwner is NavBackStackEntry) storeOwner.arguments else null
        )

        fun fromAny(owner: Any) = ViewModelOwner(
            (owner as ViewModelStoreOwner).viewModelStore,
            owner as? SavedStateRegistryOwner,
            if (owner is NavBackStackEntry) owner.arguments else null
        )
    }
}