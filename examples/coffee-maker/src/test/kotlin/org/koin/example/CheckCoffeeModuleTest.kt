package org.koin.example

import io.mockk.mockkClass
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkKoinModules
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.koin.test.verify.verify

@Category(CheckModuleTest::class)
class CheckCoffeeModuleTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz, relaxed = true)
    }

    @Test
    fun checkCoffeeModule(){
        // Dynamic Koin config verification
        // needs Mocking framework
        checkModules {
            modules(coffeeAppModule)
        }
    }

    @Test
    fun verifyCoffeeModule(){
        // Static Koin config verification
        coffeeAppModule.verify()
    }
}