package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

class RunModulesTest : AutoCloseKoinTest() {

    val moduleA = applicationContext {
        bean { ComponentA("A1") }
    }

    val moduleB = applicationContext {
        bean { ComponentB(get()) }
    }

    val moduleC = applicationContext {
        bean { ComponentA("A2") }
    }

    class ComponentA(val name : String)
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun `load several modules`() {
        startKoin(listOf(moduleA))

        Assert.assertNotNull(get<ComponentA>())

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        loadKoinModules(moduleB)
        Assert.assertNotNull(get<ComponentB>())
    }

    @Test
    fun `load and override`() {
        startKoin(listOf(moduleA))

        loadKoinModules(moduleC,moduleB)

        val b = get<ComponentB>()
        Assert.assertNotNull(b)
        val a2 = get<ComponentA>()
        Assert.assertEquals(a2, b.componentA)
        Assert.assertEquals("A2", a2.name)
    }
}