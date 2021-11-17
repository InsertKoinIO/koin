package org.koin.example

import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.perfs.perfModule400
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkKoinModules

class ChecModuleTest : KoinTest {

    @Test
    fun checkCoffeeModule(){

        checkKoinModules(perfModule400())
    }
}