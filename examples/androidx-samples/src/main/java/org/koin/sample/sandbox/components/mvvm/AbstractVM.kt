package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ViewModelImpl(val savedStateHandle: SavedStateHandle) : AbstractViewModel()
abstract class AbstractViewModel : ViewModel()