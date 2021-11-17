package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.SimpleService

class SimpleViewModel(val id: String, val service: SimpleService) : ViewModel()

class OneViewModel() : ViewModel(){
    init {
        println("ctor $this")
    }
    override fun onCleared() {
        super.onCleared()
        println("closing ViewModel $this")
    }
}

class UseOneViewModel(val vm : OneViewModel){
    init {
        println("ctor $this - $vm")
    }
}