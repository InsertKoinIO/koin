package org.koin.example

import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verifySequence
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

class CoffeeMakerTest : KoinTest {

    private val coffeeMaker: CoffeeMaker by inject()
    private val heater: Heater by inject()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz, relaxed = true)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(coffeeAppModule)
        }

        declareMock<Heater> {
            every { isHot() } returns true
        }

        coffeeMaker.brew()

        verifySequence {
            heater.on()
            heater.isHot()
            heater.off()
        }
    }
}
