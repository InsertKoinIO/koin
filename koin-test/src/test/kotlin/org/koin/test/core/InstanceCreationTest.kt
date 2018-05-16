package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

class InstanceCreationTest : AutoCloseKoinTest() {

    val FlatModule = module {
        single { ComponentA() }
        single { ComponentB(get()) }
        single { ComponentC(get(), get()) }
    }

    val HierarchicModule = module {
        single { ComponentA() }

        module("B") {
            single { ComponentB(get()) }

            module("C") {
                single { ComponentC(get(), get()) }
            }
        }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentB: ComponentB, val componentA: ComponentA)

    @Test
    fun `load and create instances for flat module`() {
        startKoin(listOf(FlatModule))

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
        assertContexts(1)
        assertIsInRootPath(ComponentA::class)
        assertIsInRootPath(ComponentB::class)
        assertIsInRootPath(ComponentC::class)
    }

    @Test
    fun `load and create instances for hierarchic context`() {
        startKoin(listOf(HierarchicModule))

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
        assertContexts(3)
        assertIsInRootPath(ComponentA::class)
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")
    }

}