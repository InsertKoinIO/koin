package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.SimpleService
import java.util.*

class SavedStateViewModel(val handle: SavedStateHandle, val id: String, val service: SimpleService) : ViewModel(){
    init {
        val get = handle.get<String>("vm1")
        println("handle: $get")
        handle.set("vm1",UUID.randomUUID().toString())
    }
}