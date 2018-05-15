package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.path.Path
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.ext.junit.assertPath

class PathTest : AutoCloseKoinTest() {

    val FlatContextsModule = module {
        module(path = "B") {
            single { ComponentA() }
            single("B_B") { ComponentB(get()) }
        }

        module(path = "C") {
            single { ComponentA() }
            single("B_C") { ComponentB(get()) }
        }
    }

    val HierarchyContextsModule = module {
        module(path = "A") {
            single { ComponentA() }

            module(path = "B") {
                single { ComponentB(get()) }

                module(path = "C") {
                    single { ComponentC(get()) }
                }
            }

        }
        module(path = "A_2") {
            single { ComponentA() }
        }
    }

    val badVisibility = module {
        module(path = "A") {
            single { ComponentA() }
        }

        single { ComponentB(get()) }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun `has flat visibility`() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(4)

        assertPath("B", Path.ROOT)
        assertPath("C", Path.ROOT)

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

        assertPath("A", Path.ROOT)
        assertPath("B", "A")
        assertPath("C", "B")

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