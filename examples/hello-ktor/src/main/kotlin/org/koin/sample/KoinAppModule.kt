package org.koin.sample

import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import org.koin.ktor.plugin.RequestScope
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

val appModule2 = module {
    singleOf(::HelloService2)
}