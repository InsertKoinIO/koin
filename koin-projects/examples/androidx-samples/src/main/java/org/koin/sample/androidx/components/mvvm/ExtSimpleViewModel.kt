package org.koin.sample.androidx.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.components.main.Service

class ExtSimpleViewModel(val service: Service) : ViewModel()