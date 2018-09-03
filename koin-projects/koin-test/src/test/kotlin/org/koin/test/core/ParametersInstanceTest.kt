package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class ParametersInstanceTest : AutoCloseKoinTest() {

    val simpleModule1 = module {
        single { (b: ComponentB) -> ComponentA(b) }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB : KoinComponent {

        val compA: ComponentA by inject { parametersOf(this) }
    }

    class ComponentC : KoinComponent {

        val compA: ComponentA by inject()
    }

    @Test
    fun `should inject parameters with factory`() {
        startKoin(listOf(simpleModule1))

        val b = ComponentB()

        assertRemainingInstanceHolders(0)
        Assert.assertNotNull(b.compA)

        assertRemainingInstanceHolders(1)
        Assert.assertEquals(b, b.compA.componentB)
    }

    @Test
    fun `missing parameters`() {
        startKoin(listOf(simpleModule1))

        val c = ComponentC()

        assertRemainingInstanceHolders(0)
        try {
            c.compA
            fail()
        } catch (e: BeanInstanceCreationException) {
            e.printStackTrace()
        }
    }
}