package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersInstanceTest : AutoCloseKoinTest() {

    val simpleModule1 = module {

        single { params -> ComponentA(params["this"]) }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB : KoinComponent {

        val compA: ComponentA by inject { mapOf("this" to this) }
    }

    class ComponentC : KoinComponent {

        val compA: ComponentA by inject()
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

    @Test
    fun `missing parameters`() {
        startKoin(listOf(simpleModule1))

        val c = ComponentC()

        assertRemainingInstances(0)
        try {
            c.compA
            fail()
        } catch (e: BeanInstanceCreationException) {
            e.printStackTrace()
        }
    }
}