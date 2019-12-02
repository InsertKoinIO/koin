package org.koin.example

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.test.check.checkModules

class CheckCoffeeMakerTest {

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {
        checkModules {
            printLogger(Level.DEBUG)
            modules(coffeeAppModule)
        }
    }
}