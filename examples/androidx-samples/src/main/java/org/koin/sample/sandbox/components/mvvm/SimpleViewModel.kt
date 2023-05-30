package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.main.SimpleService

class SimpleViewModel(val id: String, val service: SimpleService) : ViewModel()
