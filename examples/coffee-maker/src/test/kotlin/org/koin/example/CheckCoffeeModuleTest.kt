package org.koin.example

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkKoinModules

@Category(CheckModuleTest::class)
class CheckCoffeeModuleTest : KoinTest {

//    @get:Rule
//    val mockProvider = MockProviderRule.create { clazz ->
//        mockkClass(clazz, relaxed = true)
//    }

    @Test
    fun checkCoffeeModule(){

        checkKoinModules(coffeeAppModule)
    }
}