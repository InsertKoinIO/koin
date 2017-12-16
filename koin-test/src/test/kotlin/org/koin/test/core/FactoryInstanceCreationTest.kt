package org.koin.test.core

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.applicationContext
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class FactoryInstanceCreationTest : AbstractKoinTest() {

    val FlatModule =
            applicationContext {
                factory { ComponentA() }
                provide { ComponentB(get()) }
                provide { ComponentC(get(), get()) }
            }

    val HierarchicModule =
            applicationContext {
                factory { ComponentA() }

                context("B") {
                    provide { ComponentB(get()) }

                    context("C") {
                        provide { ComponentC(get(), get()) }
                    }
                }
            }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentB: ComponentB, val componentA: ComponentA)

    @Before
    fun before() {
        Koin.logger = PrintLogger()
    }

    @Test
    fun `load and create instances for flat module`() {
        startKoin(listOf(FlatModule))

        val a = get<ComponentA>()
        val b = get<ComponentB>()
        val c = get<ComponentC>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(b)
        Assert.assertNotNull(c)
        Assert.assertNotEquals(a, b.componentA)
        Assert.assertNotEquals(a, c.componentA)
        Assert.assertEquals(b, c.componentB)

        assertRemainingInstances(2)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertDefinedInScope(ComponentC::class, Scope.ROOT)
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
        Assert.assertNotEquals(a, b.componentA)
        Assert.assertNotEquals(a, c.componentA)
        Assert.assertEquals(b, c.componentB)

        assertRemainingInstances(2)
        assertDefinitions(3)
        assertContexts(3)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")
    }

}