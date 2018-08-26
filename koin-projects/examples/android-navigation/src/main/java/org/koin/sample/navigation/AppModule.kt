package org.koin.sample.navigation

import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

// uses experimental experimental builder
val appModule = module {
    viewModel<FirstViewModel>()
    viewModel<SecondViewModel>()
    viewModel<ThirdViewModel>()
    viewModel { (sharedValue: String) -> FourFiveSharedViewModel(sharedValue) }
}