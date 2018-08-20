package org.koin.sample.navigation

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    viewModel<FirstViewModel>()
    viewModel<SecondViewModel>()
    viewModel<ThirdViewModel>()
}