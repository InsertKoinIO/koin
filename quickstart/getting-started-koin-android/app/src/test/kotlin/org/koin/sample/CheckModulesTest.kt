package org.koin.sample

import androidx.lifecycle.SavedStateHandle
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TestRule
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

@Category(CheckModuleTest::class)
class CheckModulesTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun checkAllModules() = checkModules(parameters = {
        defaultValue<SavedStateHandle>()
    }) {
        modules(appModule)
    }
}