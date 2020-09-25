package org.koin.sample.androidx.compose.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.sample.androidx.compose.data.UserRepository
import org.koin.sample.androidx.compose.viewmodel.UserViewModel

val appModule = module {
    viewModel { UserViewModel(get()) }
    single { UserRepository() }
}