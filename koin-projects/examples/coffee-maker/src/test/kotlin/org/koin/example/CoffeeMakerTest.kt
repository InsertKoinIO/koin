package org.koin.example

import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CoffeeMakerTest : AutoCloseKoinTest() {

    val coffeeMaker: CoffeeMaker by inject()
    val heater: Heater by inject()

    @Before
    fun before() {
        coffeeKoinApp.start()

        declareMock<Heater> {
            given(isHot()).will { true }
        }
    }

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {

        coffeeMaker.brew()

        verify(heater, times(1)).on()
        verify(heater, times(1)).off()
    }
}