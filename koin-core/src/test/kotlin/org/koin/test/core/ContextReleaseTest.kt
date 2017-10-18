package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.error.NoScopeFoundException
import org.koin.test.ext.*

class ContextReleaseTest {

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
        val ctx = Koin().build(HierarchyContextsModule())

        ctx.assertContexts(4)
        ctx.assertDefinitions(3)

        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")

        ctx.assertScopeParent("B", "A")
        ctx.assertScopeParent("C", "B")

        val a1 = ctx.get<ComponentA>()
        val b1 = ctx.get<ComponentB>()
        val c1 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        ctx.release("B")

        ctx.assertRemainingInstances(1)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 0)
        ctx.assertContextInstances("C", 0)

        val a2 = ctx.get<ComponentA>()
        val b2 = ctx.get<ComponentB>()
        val c2 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        Assert.assertEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from A`() {
        val ctx = Koin().build(HierarchyContextsModule())

        ctx.assertContexts(4)
        ctx.assertDefinitions(3)

        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")

        ctx.assertScopeParent("B", "A")
        ctx.assertScopeParent("C", "B")

        val a1 = ctx.get<ComponentA>()
        val b1 = ctx.get<ComponentB>()
        val c1 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        ctx.release("A")

        ctx.assertRemainingInstances(0)
        ctx.assertContextInstances("A", 0)
        ctx.assertContextInstances("B", 0)
        ctx.assertContextInstances("C", 0)

        val a2 = ctx.get<ComponentA>()
        val b2 = ctx.get<ComponentB>()
        val c2 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        Assert.assertNotEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from ROOT`() {
        val ctx = Koin().build(HierarchyContextsModule())

        ctx.assertContexts(4)
        ctx.assertDefinitions(3)

        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")

        ctx.assertScopeParent("B", "A")
        ctx.assertScopeParent("C", "B")

        val a1 = ctx.get<ComponentA>()
        val b1 = ctx.get<ComponentB>()
        val c1 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        ctx.release(Scope.ROOT)

        ctx.assertRemainingInstances(0)
        ctx.assertContextInstances("A", 0)
        ctx.assertContextInstances("B", 0)
        ctx.assertContextInstances("C", 0)

        val a2 = ctx.get<ComponentA>()
        val b2 = ctx.get<ComponentB>()
        val c2 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        Assert.assertNotEquals(a1, a2)
        Assert.assertNotEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should release context - from C`() {
        val ctx = Koin().build(HierarchyContextsModule())

        ctx.assertContexts(4)
        ctx.assertDefinitions(3)

        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")

        ctx.assertScopeParent("B", "A")
        ctx.assertScopeParent("C", "B")

        val a1 = ctx.get<ComponentA>()
        val b1 = ctx.get<ComponentB>()
        val c1 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        ctx.release("C")

        ctx.assertRemainingInstances(2)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 0)

        val a2 = ctx.get<ComponentA>()
        val b2 = ctx.get<ComponentB>()
        val c2 = ctx.get<ComponentC>()

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        Assert.assertEquals(a1, a2)
        Assert.assertEquals(b1, b2)
        Assert.assertNotEquals(c1, c2)
    }

    @Test
    fun `should not release context - unknown context`() {
        val ctx = Koin().build(HierarchyContextsModule())

        ctx.assertContexts(4)
        ctx.assertDefinitions(3)

        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")

        ctx.assertScopeParent("B", "A")
        ctx.assertScopeParent("C", "B")

        Assert.assertNotNull(ctx.get<ComponentA>())
        Assert.assertNotNull(ctx.get<ComponentB>())
        Assert.assertNotNull(ctx.get<ComponentC>())

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)

        try {
            ctx.release("D")
            fail()
        } catch (e: NoScopeFoundException) {
        }

        ctx.assertRemainingInstances(3)
        ctx.assertContextInstances("A", 1)
        ctx.assertContextInstances("B", 1)
        ctx.assertContextInstances("C", 1)
    }
}