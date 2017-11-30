package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.error.NoScopeFoundException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.releaseContext
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.*

class ContextReleaseTest : AbstractKoinTest() {

    class HierarchyContextsModule() : Module() {
        override fun context() = applicationContext {
            context(name = "A") {
                provide { ComponentA() }

                context(name = "B") {
                    provide { ComponentB() }

                    context(name = "C") {
                        provide { ComponentC() }
                    }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun `should release context - from B`() {
        startKoin(listOf(HierarchyContextsModule()))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("B")

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
        startKoin(listOf(HierarchyContextsModule()))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("A")

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
        startKoin(listOf(HierarchyContextsModule()))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext(Scope.ROOT)

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
        startKoin(listOf(HierarchyContextsModule()))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("C")

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
        startKoin(listOf(HierarchyContextsModule()))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        Assert.assertNotNull(get<ComponentA>())
        Assert.assertNotNull(get<ComponentB>())
        Assert.assertNotNull(get<ComponentC>())

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        try {
            releaseContext("D")
            fail()
        } catch (e: NoScopeFoundException) {
        }

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)
    }
}