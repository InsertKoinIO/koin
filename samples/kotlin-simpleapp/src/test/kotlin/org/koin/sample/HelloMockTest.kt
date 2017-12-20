package org.koin.sample

import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AbstractKoinTest
import org.mockito.Mockito
import org.mockito.Mockito.mock

val MockModule = applicationContext {
    provide { mock(HelloService::class.java) }
}

class HelloMockTest : AbstractKoinTest() {

    val service by inject<HelloService>()

    @Before
    fun before() {
        startKoin(listOf(MockModule))
    }

    @Test
    fun tesKoinComponents() {
        HelloApplication()

        Mockito.verify(service).hello()
    }
}