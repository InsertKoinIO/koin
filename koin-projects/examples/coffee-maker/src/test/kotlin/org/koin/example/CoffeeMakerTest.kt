package org.koin.example

import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CoffeeMakerTest : MockitoTest() {

    private val coffeeMaker: CoffeeMaker by inject()
    private val heater: Heater by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(coffeeAppModule)
    }

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {
        declareMock<Heater> {
            given(isHot()).will { true }
        }

        coffeeMaker.brew()

        verify(heater, times(1)).on()
        verify(heater, times(1)).off()
    }
}
