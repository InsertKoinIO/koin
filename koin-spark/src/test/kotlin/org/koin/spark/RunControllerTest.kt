package org.koin.spark

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.dsl.path.ModulePath
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertIsInModulePath
import org.koin.test.ext.junit.assertRemainingInstances

var ctor = 0

class RunControllerTest : KoinTest {

    val module = module {
        controller { HelloController() }
    }

    class HelloController {
        init {
            ctor++
        }
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun `check controller is well injected`() {
        startKoin(listOf(module))
        runControllers()

        assertEquals(1, ctor)

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(1)
        assertIsInModulePath(HelloController::class, ModulePath.ROOT)
    }
}