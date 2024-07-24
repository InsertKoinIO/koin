package org.koin.example

import org.junit.Test
import org.koin.test.verify.verify

class CheckCoffeeModuleTest {

    @Test
    fun checkCoffeeModule() {
        coffeeAppModule.verify()
    }
}