package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

@Deprecated("Replaced by ViewModelStoreOwner")
typealias ViewModelStoreOwnerProducer = () -> ViewModelStoreOwner

@Deprecated("Replaced by ViewModelStoreOwner")
typealias ViewModelOwnerDefinition = () -> ViewModelOwner

@Deprecated("Replaced by ViewModelStoreOwner")
class ViewModelOwner(
    val storeOwner: ViewModelStoreOwner,
    val stateRegistry: SavedStateRegistryOwner? = null,
) {
    companion object {
        @Deprecated("Replaced by ViewModelStoreOwner")
        fun from(storeOwner: ViewModelStoreOwner, stateRegistry: SavedStateRegistryOwner? = null) =
            ViewModelOwner(storeOwner, stateRegistry)

        @Deprecated("Replaced by ViewModelStoreOwner")
        fun from(storeOwner: ViewModelStoreOwner) = ViewModelOwner(storeOwner)

        @Deprecated("Replaced by ViewModelStoreOwner")
        fun fromAny(owner: Any) =
            ViewModelOwner((owner as ViewModelStoreOwner), owner as? SavedStateRegistryOwner)
    }
}