package org.koin.android.viewmodel

import android.arch.lifecycle.ViewModelStore
import android.arch.lifecycle.ViewModelStoreOwner

typealias ViewModelOwnerDefinition = () -> ViewModelOwner

class ViewModelOwner(
    val store: ViewModelStore
) {
    companion object {
        fun from(store: ViewModelStore) = ViewModelOwner(store)

        fun from(storeOwner: ViewModelStoreOwner) = ViewModelOwner(storeOwner.viewModelStore)

        fun fromAny(owner: Any) = ViewModelOwner((owner as ViewModelStoreOwner).viewModelStore)
    }
}