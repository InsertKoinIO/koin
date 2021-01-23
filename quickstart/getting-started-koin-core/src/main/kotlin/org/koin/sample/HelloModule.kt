package org.koin.sample

import org.koin.dsl.module

// Koin module
val helloModule = module {
    single { HelloMessageData() }
    single<HelloService> { HelloServiceImpl(get()) }
}