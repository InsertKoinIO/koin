package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.path.ModulePath
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances


class InterfaceBindingTest : AutoCloseKoinTest() {

    val InterfacesModule = module {
        single { ComponentA() as InterfaceComponent }
        single("B") { ComponentB() as OtherInterfaceComponent }
        single("C") { ComponentC() as OtherInterfaceComponent }
    }

    interface InterfaceComponent
    class ComponentA : InterfaceComponent

    interface OtherInterfaceComponent
    class ComponentB : OtherInterfaceComponent
    class ComponentC : OtherInterfaceComponent

    @Test
    fun `should get from interface but not implementation`() {
        startKoin(listOf(InterfacesModule))

        val a = get<InterfaceComponent>()
        Assert.assertNotNull(a)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(1)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(InterfaceComponent::class, ModulePath.ROOT)
    }

    @Test
    fun `should get two components from interface only`() {
        startKoin(listOf(InterfacesModule))

        val b = get<OtherInterfaceComponent>("B")
        val c = get<OtherInterfaceComponent>("C")
        Assert.assertNotNull(b)
        Assert.assertNotNull(c)

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentC>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<OtherInterfaceComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(2)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(OtherInterfaceComponent::class, ModulePath.ROOT)
    }
}