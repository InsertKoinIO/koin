package org.koin.sample.androidx.compose.di

import org.koin.dsl.module
import org.koin.sample.androidx.compose.data.UserRepository

val appModule = module {
    single { UserRepository() }
}