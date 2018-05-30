package org.koin.test.core


import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.dsl.path.Path
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

class MultipleModuleTest : AutoCloseKoinTest() {

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA, val componentB: ComponentB)

    val SimpleModuleA = module {
        single { ComponentA() }
    }

    val SimpleModuleB = module {
        single { ComponentB(get()) }
    }

    val SimpleModuleC = module {
        module(path = "C") {
            single { ComponentC(get(), get()) }
        }
    }

    @Test
    fun `load mulitple modules`() {
        startKoin(listOf(SimpleModuleA, SimpleModuleB, SimpleModuleC))

        assertRemainingInstances(0)
        assertDefinitions(3)
        assertContexts(2)

        assertNotNull(get<ComponentA>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentC>())

        val a = get<ComponentA>()
        val b = get<ComponentB>()
        val c = get<ComponentC>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(b)
        Assert.assertNotNull(c)
        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(a, c.componentA)
        Assert.assertEquals(b, c.componentB)

        assertRemainingInstances(3)
        assertDefinitions(3)
        assertContexts(2)
        assertIsInRootPath(ComponentA::class)
        assertIsInRootPath(ComponentB::class)
        assertIsInModulePath(ComponentC::class, "C")
    }
}