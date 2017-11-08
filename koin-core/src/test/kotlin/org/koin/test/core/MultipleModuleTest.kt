package org.koin.test.core


import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinedInScope
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances

class MultipleModuleTest {

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA, val componentB: ComponentB)

    class SimpleModuleA() : Module() {
        override fun context() = applicationContext {
            context(name = "A") {
                provide { ComponentA() }
            }
        }
    }

    class SimpleModuleB() : Module() {
        override fun context() = applicationContext {
            context(name = "B") {
                provide { ComponentB(get()) }
            }
        }
    }

    class SimpleModuleC() : Module() {
        override fun context() = applicationContext {
            context(name = "C") {
                provide { ComponentC(get(), get()) }
            }
        }
    }


    @Test
    fun `load mulitple modules`() {
        val ctx = Koin().build(listOf(SimpleModuleA(), SimpleModuleB(), SimpleModuleC()))

        ctx.assertRemainingInstances(0)
        ctx.assertDefinitions(3)
        ctx.assertContexts(4)

        assertNotNull(ctx.get<ComponentA>())
        assertNotNull(ctx.get<ComponentB>())
        assertNotNull(ctx.get<ComponentC>())

        val a = ctx.get<ComponentA>()
        val b = ctx.get<ComponentB>()
        val c = ctx.get<ComponentC>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(b)
        Assert.assertNotNull(c)
        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(a, c.componentA)
        Assert.assertEquals(b, c.componentB)

        ctx.assertRemainingInstances(3)
        ctx.assertDefinitions(3)
        ctx.assertContexts(4)
        ctx.assertDefinedInScope(ComponentA::class, "A")
        ctx.assertDefinedInScope(ComponentB::class, "B")
        ctx.assertDefinedInScope(ComponentC::class, "C")
    }
}