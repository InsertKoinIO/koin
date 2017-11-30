package org.koin.sample

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sample.Property.WHO
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AbstractKoinTest
import org.koin.test.KoinTest

class HelloAppTest : AbstractKoinTest() {

    val model by inject<HelloModel>()
    val service by inject<HelloServiceImpl>()

    val testVal = "TEST"

    @Before
    fun before() {
        Koin.logger = PrintLogger()
        startKoin(listOf(HelloModule()), properties = mapOf(WHO to testVal))
    }

    @Test
    fun testInjectedComponents() {
        assertEquals(model, service.helloModel)
        assertEquals(model.who, testVal)
    }

    @Test
    fun tesKoinComponents() {
        val helloApp = HelloApp()
        assertEquals(service, helloApp.helloService)
    }
}