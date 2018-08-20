package org.koin.example

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.log.EmptyLogger
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times

class CoffeeMakerTest : KoinTest {

    val heater: Heater by inject()
    val coffeeMaker: CoffeeMaker by inject()

    @Before
    fun before() {
        startKoin(listOf(coffeeMakerModule))
        declareMock<Heater>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {
        given(heater.isHot()).will { true }
        coffeeMaker.brew()
        Mockito.verify(heater, times(1)).on()
        Mockito.verify(heater, times(1)).off()
    }

}