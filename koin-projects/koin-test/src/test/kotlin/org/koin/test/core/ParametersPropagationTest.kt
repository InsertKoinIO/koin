package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersPropagationTest : AutoCloseKoinTest() {

    val simpleModule1 = module {
        single { (c : ComponentC) -> ComponentA(c) }
        single { params -> ComponentB(get { params }) }
    }

    class ComponentA(val componentC: ComponentC)
    class ComponentB(val componentA: ComponentA)
    class ComponentC : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val componentB: ComponentB by inject { parametersOf(this) }
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