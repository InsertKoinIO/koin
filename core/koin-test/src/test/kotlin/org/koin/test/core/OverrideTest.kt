package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

class OverrideTest : AutoCloseKoinTest() {

    val sampleModule1 = module {
        single { ComponentA() } bind MyInterface::class
        single { ComponentA() }
    }

    val sampleModule2 = module {
        single("A") { ComponentA() as MyInterface }
        single("B") { ComponentB() as MyInterface }
    }

    val sampleModule3 = module {
        single { ComponentB() as MyInterface }
        single { ComponentA() as MyInterface }
    }

    val sampleModule4 = module {
        single { ComponentB() as MyInterface }
        factory { ComponentA() as MyInterface }
    }

    class ComponentA : MyInterface
    class ComponentB : MyInterface
    interface MyInterface

    @Test
    fun `override provide with bind`() {
        startKoin(listOf(sampleModule1))

        assertNotNull(get<ComponentA>())

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun `no override but conflicting def`() {
        startKoin(listOf(sampleModule2))

        assertNotEquals(get<MyInterface>("A"), get<MyInterface>("B"))
    }

    @Test
    fun `override provide with bean`() {
        startKoin(listOf(sampleModule3))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentA)

    }

    @Test
    fun `override provide with factory`() {
        startKoin(listOf(sampleModule4))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentA)
        assertNotEquals(intf, get<MyInterface>())
    }
}