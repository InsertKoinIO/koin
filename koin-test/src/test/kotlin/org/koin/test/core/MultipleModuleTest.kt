package org.koin.test.core


import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class MultipleModuleTest : AbstractKoinTest() {

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA, val componentB: ComponentB)

    class SimpleModuleA() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA() }
        }
    }

    class SimpleModuleB() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB(get()) }
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
        startKoin(listOf(SimpleModuleA(), SimpleModuleB(), SimpleModuleC()))

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
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertDefinedInScope(ComponentC::class, "C")
    }
}