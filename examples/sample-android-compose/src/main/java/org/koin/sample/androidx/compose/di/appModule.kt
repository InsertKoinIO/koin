package org.koin.sample.androidx.compose.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.sample.androidx.compose.MainActivity
import org.koin.sample.androidx.compose.data.*
import org.koin.sample.androidx.compose.viewmodel.SSHViewModel
import org.koin.sample.androidx.compose.viewmodel.UserViewModel

val appModule = module {
    viewModelOf(::UserViewModel)
    singleOf(::UserRepository)
    factoryOf(::MyFactory)
    single { MySingle() }
    viewModelOf(::SSHViewModel)
}

val WALLET_SCOPE = named("wallet")
val secondModule = module {
    scope<MyFactory>{
        factoryOf(::MyInnerFactory)
        scoped { MyScoped() }
    }
    scope<MainActivity> {
        factoryOf(::MyInnerFactory)
        scoped { MyScoped() }
    }
}