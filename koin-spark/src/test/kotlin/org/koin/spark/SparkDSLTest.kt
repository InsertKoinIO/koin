package org.koin.spark

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class SparkDSLTest : KoinTest {

    val controller: HelloController by inject()
    val service: HelloService by inject()

    val module = applicationContext {
        bean { HelloService() }
        controller { HelloController(get()) }
    }

    class HelloService
    class HelloController(val service: HelloService)

    @After
    fun after() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun `check controller is well injected`() {
        startKoin(listOf(module))

        assertEquals(service, controller.service)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(HelloController::class, Scope.ROOT)
        assertDefinedInScope(HelloService::class, Scope.ROOT)

        assertEquals(controller, get<SparkController>())
    }
}