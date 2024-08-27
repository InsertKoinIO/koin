package org.koin.sample.sandbox.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.*

class NavViewModel2(private val savedStateHandle: SavedStateHandle) : ViewModel(){

    init {
        println("* NavViewModel2 *")
    }

    val id : String = UUID.randomUUID().toString()
    override fun toString(): String = "NavViewModel2[id:$id]"

    val argument: String? = savedStateHandle.get<String>("argKey")
}