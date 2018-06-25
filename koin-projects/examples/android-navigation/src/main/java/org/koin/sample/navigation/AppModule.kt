package org.koin.sample.navigation

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.sample.navigation.ThirdViewModel

val appModule = module {
    module("org.sample.fragments") {
        viewModel(isSingleton = true) { FirstViewModel() }
    }
    viewModel { SecondViewModel() }
    viewModel { ThirdViewModel() }
}