package org.koin.sample.android.components.mvvm

import android.arch.lifecycle.ViewModel
import org.koin.sample.android.components.main.Service

class ExtSimpleViewModel(val service: Service) : ViewModel()