package org.koin.sample

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.RequestScope

val appModule = module {
    singleOf(::HelloServiceImpl){
        bind<HelloService>()
        createdAtStart()
    }
    singleOf(::HelloRepository)

    scope<RequestScope>{
        scopedOf(::ScopeComponent)
    }
}

val appModule2 = module {
    singleOf(::HelloService2)
}