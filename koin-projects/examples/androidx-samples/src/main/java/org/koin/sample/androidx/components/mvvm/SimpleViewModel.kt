package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.Service

class SimpleViewModel(val id: String, val service: Service) : ViewModel()