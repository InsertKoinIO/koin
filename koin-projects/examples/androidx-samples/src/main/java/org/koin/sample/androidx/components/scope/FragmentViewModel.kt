package org.koin.sample.androidx.components.scope

import android.graphics.Color
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class FragmentViewModel: ViewModel() {
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val currentTitle = MutableLiveData<String>().apply { value = "not set yet" }
    val title = MediatorLiveData<String>().apply {
        addSource(currentTitle) { value = it }
        addSource(isLoading) { loading ->
            if (loading) {
                value = "..."
            }
        }
    }
}