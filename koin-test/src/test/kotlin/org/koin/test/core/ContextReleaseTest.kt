package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.path.Path
import org.koin.dsl.module.module
import org.koin.error.NoModulePathException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.release
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

class ContextReleaseTest : AutoCloseKoinTest() {

    val HierarchyContextsModule = module {
        module(path = "A") {
            single { ComponentA() }

            module(path = "B") {
                single { ComponentB() }

                module(path = "C") {
                    single { ComponentC() }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun `should release context - from B`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", "A")
        assertPath("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        release("B")

        assertRemainingInstances(1)
        assertContextInstances("A", 1)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        Assert.assertEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from A`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", "A")
        assertPath("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        release("A")

        assertRemainingInstances(0)
        assertContextInstances("A", 0)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        Assert.assertNotEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from ROOT`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", "A")
        assertPath("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        release(Path.ROOT)

        assertRemainingInstances(0)
        assertContextInstances("A", 0)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        Assert.assertNotEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from C`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", "A")
        assertPath("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        release("C")

        assertRemainingInstances(2)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        Assert.assertEquals(a1, a2)
        Assert.assertEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should not release context - unknown context`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", "A")
        assertPath("C", "B")

        Assert.assertNotNull(get<ComponentA>())
        Assert.assertNotNull(get<ComponentB>())
        Assert.assertNotNull(get<ComponentC>())

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        try {
            release("D")
            fail()
        } catch (e: NoModulePathException) {
        }

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)
    }
}