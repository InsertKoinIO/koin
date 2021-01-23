package org.koin.sample

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class CheckModulesTest {

    @Test
    fun `check modules`() = checkModules {
        modules(helloAppModule)
    }
}