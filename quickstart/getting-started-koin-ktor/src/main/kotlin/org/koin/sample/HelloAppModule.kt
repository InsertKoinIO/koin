package org.koin.sample

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single

val helloAppModule = module(createdAtStart = true) {
    single<HelloServiceImpl>() bind HelloService::class
    single<HelloRepository>()
}
