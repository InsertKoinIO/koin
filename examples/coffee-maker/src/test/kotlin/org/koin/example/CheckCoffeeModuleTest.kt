package org.koin.example

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.category.CheckModuleTest
import org.koin.test.verify.verify

@Category(CheckModuleTest::class)
class CheckCoffeeModuleTest {

    @Test
    fun checkCoffeeModule() {
        coffeeAppModule.verify()
    }
}