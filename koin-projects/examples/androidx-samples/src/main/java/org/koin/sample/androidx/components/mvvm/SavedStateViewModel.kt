package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.Service

class SavedStateViewModel(val handle: SavedStateHandle, val id: String, val service: Service) : ViewModel()