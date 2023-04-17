package org.koin.sample.sandbox.navigation

import androidx.lifecycle.ViewModel
import java.util.*

class NavViewModel() : ViewModel(){
    val id : String = UUID.randomUUID().toString()
    override fun toString(): String = "MainViewModel[id:$id]"
}