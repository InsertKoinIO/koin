package org.koin.sample

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::HelloServiceImpl){
        bind<HelloService>()
        createdAtStart()
    }
    singleOf(::HelloRepository)
}

val appModule2 = module {
    singleOf(::HelloService2)
}