package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

class OverrideTest : AutoCloseKoinTest() {

    val sampleModule1 = applicationContext {
        bean { ComponentA() } bind MyInterface::class
        bean { ComponentA() }
    }

    val sampleModule2 = applicationContext {
        bean("A") { ComponentA() as MyInterface }
        bean("B") { ComponentB() as MyInterface }
    }

    val sampleModule3 = applicationContext {
        bean { ComponentB() as MyInterface }
        bean { ComponentA() as MyInterface }
    }

    val sampleModule4 = applicationContext {
        bean { ComponentB() as MyInterface }
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