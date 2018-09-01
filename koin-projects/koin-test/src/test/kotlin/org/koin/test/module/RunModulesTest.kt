package org.koin.test.module

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

class RunModulesTest : AutoCloseKoinTest() {

    val moduleA = module {
        single { ComponentA("A1") }
    }

    val moduleB = module {
        single { ComponentB(get()) }
    }

    val moduleC = module {
        single(override = true) { ComponentA("A2") }
    }

//    val moduleD = module {
//        module("D") {
//            single { ComponentA("D") }
//        }
//    }

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

        Assert.assertNotNull("A2", get<ComponentB>().componentA.name)
        Assert.assertNotNull("A2", get<ComponentA>().name)
    }

//    @Test
//    fun `several loads and override with contexts`() {
//        startKoin(listOf(moduleD))
//
//        assertRemainingInstanceHolders(0)
//        assertDefinitions(1)
//        assertContexts(2)
//
//        loadKoinModules(moduleD)
//
//        assertRemainingInstanceHolders(0)
//        assertDefinitions(1)
//        assertContexts(2)
//
//        Assert.assertNotNull(get<ComponentA>())
//        assertRemainingInstanceHolders(1)
//
//        loadKoinModules(moduleD)
//
//        assertRemainingInstanceHolders(1)
//        assertDefinitions(1)
//        assertContexts(2)
//    }
}