package org.koin.sample

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.module.requestScope

val appModule = module {
    singleOf(::HelloServiceImpl){
        bind<HelloService>()
        createdAtStart()
    }
    singleOf(::HelloRepository)

    requestScope {
        scopedOf(::ScopeComponent)
    }
}