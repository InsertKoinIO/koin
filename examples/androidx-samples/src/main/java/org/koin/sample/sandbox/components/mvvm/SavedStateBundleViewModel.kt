package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.main.SimpleService

class SavedStateBundleViewModel(val handle: SavedStateHandle, val service: SimpleService) : ViewModel(){
    var result : String? = null
    init {
        result = handle.get<String>("id")
    }
}