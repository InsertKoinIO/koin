package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.SimpleService
import java.util.*

class SavedStateBundleViewModel(handle: SavedStateHandle, val service: SimpleService) : ViewModel(){
    var result : String? = null
    init {
        result = handle.get<String>("id")
    }
}