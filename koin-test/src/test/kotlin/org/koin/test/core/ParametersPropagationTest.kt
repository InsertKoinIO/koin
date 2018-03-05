package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.parameter.valuesOf
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersPropagationTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {
        bean { params -> ComponentA(params["this"]) }
        bean { params -> ComponentB(get(parameters = params.values)) }
    }

    class ComponentA(val componentC: ComponentC)
    class ComponentB(val componentA: ComponentA)
    class ComponentC : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val componentB: ComponentB by inject(parameters = valuesOf("this" to this))
    }

    @Test
    fun `should inject and propagate parameters`() {
        startKoin(listOf(simpleModule1))

        val c = ComponentC()

        assertRemainingInstances(0)
        Assert.assertNotNull(c.componentB.componentA)

        assertRemainingInstances(2)
        Assert.assertEquals(c, c.componentB.componentA.componentC)
    }
}