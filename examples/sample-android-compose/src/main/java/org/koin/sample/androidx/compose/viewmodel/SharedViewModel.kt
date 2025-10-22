package org.koin.sample.androidx.compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SharedViewModel : ViewModel() {

    var lastUpdatedTime by mutableStateOf<String?>(null)
        private set

    fun recordTime() {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        lastUpdatedTime = formatter.format(Date())
    }
}
