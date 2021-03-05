package org.koin.example

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class CheckCoffeeModuleTest : KoinTest {

    @Test
    fun checkCoffeeModule() =
            checkModules {
                modules(coffeeAppModule)
            }
}