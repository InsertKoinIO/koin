package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.main.SimpleService
import java.util.*

class SavedStateViewModel(val handle: SavedStateHandle, val id: String, val service: SimpleService) : ViewModel(){
    init {
        val get = handle.get<String>(id)
        println("handle: $get")
        handle[id] = UUID.randomUUID().toString()
    }
}