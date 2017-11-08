package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.test.ext.*


class AdditionalBindingTest {

    class BoundModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA() } bind (InterfaceComponent::class)
        }
    }

    class NotBoundModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA() }
        }
    }

    class GenericBoundModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB() } bind (OtherInterfaceComponent::class)
        }
    }

    class TwoBoundModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB() } bind (OtherInterfaceComponent::class)
            provide { ComponentC() } bind (OtherInterfaceComponent::class)
        }
    }

    class ComponentA : InterfaceComponent
    interface InterfaceComponent

    class ComponentB : OtherInterfaceComponent<String> {
        override fun get() = "HELLO"

    }

    class ComponentC : OtherInterfaceComponent<String> {
        override fun get() = "HELLO_C"

    }

    interface OtherInterfaceComponent<T> {
        fun get(): T
    }

    @Test
    fun `same instance for provided & bound component`() {
        val ctx = Koin().build(listOf(BoundModule()))

        val a = ctx.get<ComponentA>()
        val intf = ctx.get<InterfaceComponent>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(intf)
        Assert.assertEquals(a, intf)

        ctx.assertRemainingInstances(1)
        ctx.assertDefinitions(1)
        ctx.assertContexts(1)
        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
    }

    @Test
    fun `should not bound component`() {
        val ctx = Koin().build(listOf(NotBoundModule()))

        val a = ctx.get<ComponentA>()
        val intf = ctx.getOrNull<InterfaceComponent>()

        Assert.assertNotNull(a)
        Assert.assertNull(intf)

        ctx.assertRemainingInstances(1)
        ctx.assertDefinitions(1)
        ctx.assertContexts(1)
        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
    }

    @Test
    fun `should bind generic component`() {
        val ctx = Koin().build(listOf(GenericBoundModule()))

        val b = ctx.get<ComponentB>()
        val intf = ctx.get<OtherInterfaceComponent<String>>()

        Assert.assertNotNull(b)
        Assert.assertNotNull(intf)
        Assert.assertEquals("HELLO", intf.get())

        ctx.assertRemainingInstances(1)
        ctx.assertDefinitions(1)
        ctx.assertContexts(1)
        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

    @Test
    fun `should not bind generic component`() {
        val ctx = Koin().build(listOf(TwoBoundModule()))

        val intf = ctx.getOrNull<OtherInterfaceComponent<String>>()

        Assert.assertNull(intf)

        ctx.assertRemainingInstances(0)
        ctx.assertDefinitions(2)
        ctx.assertContexts(1)
        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
        ctx.assertDefinedInScope(ComponentC::class, Scope.ROOT)
    }

}