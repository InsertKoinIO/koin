package org.koin.sample.androidx.compose.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable

@OptIn(SavedStateHandleSaveableApi::class)
class SSHViewModel(val id: String,) : ViewModel() {

//    var lastIds by savedStateHandle.saveable {
//        mutableStateOf<Set<String>>(emptySet())
//    }
//
//    fun saveId(){
//        lastIds = lastIds + id
//    }

    init {
        println("$this created '$id'")
    }
}