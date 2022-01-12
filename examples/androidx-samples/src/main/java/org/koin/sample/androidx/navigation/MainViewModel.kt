package org.koin.sample.androidx.navigation

import androidx.lifecycle.ViewModel
import java.util.*

class NavViewModel() : ViewModel(){
    val id : String = UUID.randomUUID().toString()
    override fun toString(): String = "MainViewModel[id:$id]"
}