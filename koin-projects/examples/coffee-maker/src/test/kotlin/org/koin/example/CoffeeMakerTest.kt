package org.koin.example

import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CoffeeMakerTest : KoinTest {

    private val coffeeMaker: CoffeeMaker by inject()
    private val heater: Heater by inject()

    @get:Rule
    val koinRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(coffeeAppModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
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
