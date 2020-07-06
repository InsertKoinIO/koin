package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

typealias ViewModelOwnerDefinition = () -> ViewModelOwner

class ViewModelOwner(
    val store: ViewModelStore,
    val stateRegistry: SavedStateRegistryOwner? = null
) {
    companion object {
        fun from(store: ViewModelStore, stateRegistry: SavedStateRegistryOwner? = null) = ViewModelOwner(
            store,
            stateRegistry)

        fun from(storeOwner: ViewModelStoreOwner, stateRegistry: SavedStateRegistryOwner? = null) = ViewModelOwner(
            storeOwner.viewModelStore, stateRegistry)

        fun fromAny(owner: Any) = ViewModelOwner((owner as ViewModelStoreOwner).viewModelStore,
            owner as? SavedStateRegistryOwner)
    }
}