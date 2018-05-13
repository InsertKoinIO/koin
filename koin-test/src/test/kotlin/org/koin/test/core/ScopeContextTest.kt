package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.ext.junit.assertScopeParent

class ScopeContextTest : AutoCloseKoinTest() {

    val FlatContextsModule = module {
        module(path = "B") {
            bean { ComponentA() }
            bean("B_B") { ComponentB(get()) }
        }

        module(path = "C") {
            bean { ComponentA() }
            bean("B_C") { ComponentB(get()) }
        }
    }

    val HierarchyContextsModule = module {
        module(path = "A") {
            bean { ComponentA() }

            module(path = "B") {
                bean { ComponentB(get()) }

                module(path = "C") {
                    bean { ComponentC(get()) }
                }
            }

        }
        module(path = "A_2") {
            bean { ComponentA() }
        }
    }

    val badVisibility = module {
        module(path = "A") {
            bean { ComponentA() }
        }

        bean { ComponentB(get()) }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun `has flat visibility`() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(4)

        assertScopeParent("B", Scope.ROOT)
        assertScopeParent("C", Scope.ROOT)

        Assert.assertNotNull(get<ComponentB>("B_B"))
        Assert.assertNotNull(get<ComponentB>("B_C"))
        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `hierarchial visibility`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(5)
        assertDefinitions(4)

        assertScopeParent("A", Scope.ROOT)
        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val c = get<ComponentC>()
        Assert.assertNotNull(c)
        val b = get<ComponentB>()
        Assert.assertNotNull(b)
        Assert.assertEquals(b.componentA, c.componentA)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `bad visibility`() {
        startKoin(listOf(badVisibility))

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        assertRemainingInstances(0)
    }

}