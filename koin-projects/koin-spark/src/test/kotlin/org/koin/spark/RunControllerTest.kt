package org.koin.spark

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertIsInRootPath
import org.koin.test.ext.junit.assertRemainingInstanceHolders

var ctor = 0

class RunControllerTest : KoinTest {

    val module = module {
        controller { HelloController() }
    }

    class HelloController : SparkController {
        init {
            ctor++
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `check controller is well injected`() {
        startKoin(listOf(module))
//        runControllers()

        assertEquals(1, ctor)

        assertRemainingInstanceHolders(1)
        assertDefinitions(1)
        assertContexts(1)
        assertIsInRootPath(HelloController::class)
    }
}