package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersInstanceTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {

        bean { params -> ComponentA(params["this"]) }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val compA: ComponentA by inject(parameters = mapOf("this" to this))
    }

    @Test
    fun `should inject parameters with factory`() {
        startKoin(listOf(simpleModule1))

        val b = ComponentB()

        assertRemainingInstances(0)
        Assert.assertNotNull(b.compA)

        assertRemainingInstances(1)
        Assert.assertEquals(b, b.compA.componentB)
    }
}