package org.koin.example

import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CoffeeMakerTest : AutoCloseKoinTest() {

    private val coffeeMaker: CoffeeMaker by inject()
    private val heater: Heater by inject()

    @Before
    fun before() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(coffeeAppModule)
        }

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