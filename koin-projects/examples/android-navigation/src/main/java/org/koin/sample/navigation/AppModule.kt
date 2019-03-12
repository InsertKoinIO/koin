package org.koin.sample.navigation

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

// uses experimental experimental builder
val appModule = module {
    scope(named<MainActivity>()) {
        scoped { MainData() }
    }
    viewModel { FirstViewModel() }
    viewModel { SecondViewModel() }
    scope(named<ThirdView>()) {
        scoped { ThirdViewModel() }
    }
    viewModel { (sharedValue: String) -> FourFiveSharedViewModel(sharedValue) }
}