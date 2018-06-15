package org.koin.sample

import org.koin.dsl.module.module
import org.mockito.Mockito.mock

val helloMockModule = module {
    bean { mock(HelloService::class.java) }
}