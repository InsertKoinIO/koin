package org.koin.sample.androidx.components.scope

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel: ViewModel() {
    val toolbarColorLiveData = MutableLiveData<Int>().apply { value = Color.BLUE }
}