package org.koin.example

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.time.measureDuration
import org.koin.test.check.checkModules

class CheckCoffeeMakerTest {

    @Test
    fun testHeaterIsTurnedOnAndThenOff() {
        measureDuration("Check modules") {
            checkModules {
                modules(coffeeAppModule)
            }
        }
    }
}