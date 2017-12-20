package org.koin.sample

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class HelloServiceTest : KoinTest {

    val service: HelloService by inject()
    val repository: HelloRepository by inject()

    @Before()
    fun before() {
        startKoin(listOf(module))
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun `service say hello`() {
        assertEquals("Hello Spark & Koin !", service.sayHello())
    }

    @Test
    fun `repository has hello`() {
        assertEquals("Spark & Koin", repository.getHello())
    }
}