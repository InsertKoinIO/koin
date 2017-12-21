package org.koin.sample

import org.koin.dsl.module.applicationContext
import org.mockito.Mockito.mock

val helloMockModule = applicationContext {
    bean { mock(HelloService::class.java) }
}