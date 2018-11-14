package org.koin.sample.navigation

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

// uses experimental experimental builder
val appModule = module {
    viewModel { FirstViewModel() }
    viewModel { SecondViewModel() }
    viewModel { ThirdViewModel() }
    viewModel { (sharedValue: String) -> FourFiveSharedViewModel(sharedValue) }
}