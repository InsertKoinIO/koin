package org.koin.sample

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sample.Property.WHO
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AbstractKoinTest

class HelloAppTest : AbstractKoinTest() {

    val model by inject<HelloModel>()
    val service by inject<HelloService>()

    val testVal = "TEST"

    @Before
    fun before() {
        Koin.logger = PrintLogger()
        startKoin(listOf(HelloModule), properties = mapOf(WHO to testVal))
    }

    @Test
    fun testInjectedComponents() {
        assertEquals(model.who, testVal)
        assertEquals("Hello " + model.who, service.hello())
    }

    @Test
    fun tesKoinComponents() {
        val helloApp = HelloApplication()
        assertEquals(service, helloApp.helloService)
    }
}