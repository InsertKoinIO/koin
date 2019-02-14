package org.koin.sample.navigation

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

// uses experimental experimental builder
val appModule = module {
    scope<MainActivity> {
        scoped { MainData() }
    }
    viewModel { FirstViewModel() }
    viewModel { SecondViewModel() }
    scope<ThirdView> {
        scoped { ThirdViewModel() }
    }
    viewModel { (sharedValue: String) -> FourFiveSharedViewModel(sharedValue) }
}