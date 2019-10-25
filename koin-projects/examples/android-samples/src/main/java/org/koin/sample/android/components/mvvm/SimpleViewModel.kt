package org.koin.sample.android.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.android.components.main.Service

class SimpleViewModel(val id: String, val service: Service) : ViewModel()