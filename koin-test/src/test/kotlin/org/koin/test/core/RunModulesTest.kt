package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class RunModulesTest : AutoCloseKoinTest() {

    val moduleA = applicationContext {
        bean { ComponentA("A1") }
    }

    val moduleB = applicationContext {
        bean { ComponentB(get()) }
    }

    val moduleC = applicationContext(override = true) {
        bean { ComponentA("A2") }
    }

    val moduleD = applicationContext {
        context("D", override = true) {
            bean { ComponentA("D") }
        }
    }

    class ComponentA(val name: String)
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

        loadKoinModules(moduleC, moduleB)

        val b = get<ComponentB>()
        Assert.assertNotNull(b)
        val a2 = get<ComponentA>()
        Assert.assertEquals(a2, b.componentA)
        Assert.assertEquals("A2", a2.name)
    }

    @Test
    fun `several loads and override with contexts`() {
        startKoin(listOf(moduleD))

        assertRemainingInstances(0)
        assertDefinitions(1)
        assertContexts(2)

        loadKoinModules(moduleD)

        assertRemainingInstances(0)
        assertDefinitions(1)
        assertContexts(2)

        Assert.assertNotNull(get<ComponentA>())
        assertRemainingInstances(1)

        loadKoinModules(moduleD)

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(2)
    }
}
