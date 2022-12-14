package org.koin.sample.androidx.compose.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.sample.androidx.compose.data.MyFactory
import org.koin.sample.androidx.compose.data.MySingle
import org.koin.sample.androidx.compose.data.UserRepository
import org.koin.sample.androidx.compose.viewmodel.SSHViewModel
import org.koin.sample.androidx.compose.viewmodel.UserViewModel

val appModule = module {
    viewModelOf(::UserViewModel)
    singleOf(::UserRepository)
    factory { (id: String) -> MyFactory(id) }
    single { MySingle() }
    viewModelOf(::SSHViewModel)
}