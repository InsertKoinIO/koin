package org.koin.sample.androidx.navigation

import androidx.lifecycle.ViewModel
import java.util.*

class NavViewModel(val id : String = UUID.randomUUID().toString()) : ViewModel(){
    override fun toString(): String = "MainViewModel[id:$id]"
}