package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.DefinitionBindingException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertIsInRootPath
import org.koin.test.ext.junit.assertRemainingInstanceHolders


class AdditionalBindingTest : AutoCloseKoinTest() {

    val BoundModule = module {
        single { ComponentA() } bind InterfaceComponent::class
    }

    val NotBoundModule = module {
        single { ComponentA() }
    }

    val GenericBoundModule = module {
        single { ComponentB() } bind OtherInterfaceComponent::class
    }

    val TwoBoundModule = module {
        single { ComponentB() } bind OtherInterfaceComponent::class
        single { ComponentC() } bind OtherInterfaceComponent::class
    }

    val badModule = module {
        single { ComponentA() } bind InterfaceComponent2::class
    }

    class ComponentA : InterfaceComponent
    interface InterfaceComponent
    interface InterfaceComponent2

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
        startKoin(listOf(BoundModule))

        val a = get<ComponentA>()
        val intf = get<InterfaceComponent>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(intf)
        Assert.assertEquals(a, intf)

        assertRemainingInstanceHolders(1)
        assertDefinitions(1)
        assertContexts(1)
        assertIsInRootPath(ComponentA::class)
    }

    @Test
    fun `should not bound component`() {
        startKoin(listOf(NotBoundModule))

        val a = get<ComponentA>()

        try {
            get<InterfaceComponent>()
            fail()
        } catch (e: Exception) {
        }

        Assert.assertNotNull(a)

        assertRemainingInstanceHolders(1)
        assertDefinitions(1)
        assertContexts(1)
        assertIsInRootPath(ComponentA::class)
    }

    @Test
    fun `should bind generic component`() {
        startKoin(listOf(GenericBoundModule))

        val b = get<ComponentB>()
        val intf = get<OtherInterfaceComponent<String>>()

        Assert.assertNotNull(b)
        Assert.assertNotNull(intf)
        Assert.assertEquals("HELLO", intf.get())

        assertRemainingInstanceHolders(1)
        assertDefinitions(1)
        assertContexts(1)
        assertIsInRootPath(ComponentB::class)
    }

    @Test
    fun `should not bind generic component`() {
        startKoin(listOf(TwoBoundModule))

        try {
            get<OtherInterfaceComponent<String>>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstanceHolders(0)
        assertDefinitions(2)
        assertContexts(1)
        assertIsInRootPath(ComponentB::class)
        assertIsInRootPath(ComponentC::class)
    }

    @Test
    fun `should not bind non parent class`() {
        try {
            startKoin(listOf(badModule))
            fail()
        } catch (e: DefinitionBindingException) {
        }
    }
}