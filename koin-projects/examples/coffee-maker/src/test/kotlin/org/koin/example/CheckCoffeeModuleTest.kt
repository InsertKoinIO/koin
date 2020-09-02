package org.koin.example

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class CheckCoffeeModuleTest : MockitoTest() {

    @Test
    fun checkCoffeeModule() =
            checkModules {
                modules(coffeeAppModule)
            }
}