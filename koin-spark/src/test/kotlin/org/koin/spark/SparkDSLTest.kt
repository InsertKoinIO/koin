package org.koin.spark

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class SparkDSLTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean { HelloService() }
        controller { HelloController(get()) }
    }

    class HelloService
    class HelloController(val service: HelloService)

    @Test
    fun `check controller is well injected`() {
        startKoin(listOf(module))

        val controller: HelloController = get()
        val service: HelloService = get()

        assertEquals(service, controller.service)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(HelloController::class, Scope.ROOT)
        assertDefinedInScope(HelloService::class, Scope.ROOT)

        assertEquals(controller, get<SparkController>())
    }

    @Test
    fun `check controller is a bean`() {
        startKoin(listOf(module))

        val service: HelloService = get()
        val controller: HelloController = get()
        val controller2: HelloController = get()

        assertEquals(service, controller.service)
        assertEquals(controller2, controller)

        assertEquals(controller, get<SparkController>())
        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(HelloController::class, Scope.ROOT)
        assertDefinedInScope(HelloService::class, Scope.ROOT)
    }
}