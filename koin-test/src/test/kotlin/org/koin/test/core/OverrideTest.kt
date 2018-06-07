package org.koin.test.core

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.error.BeanOverrideException
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.releaseContext
import org.koin.test.AutoCloseKoinTest

class OverrideTest : AutoCloseKoinTest() {

    val sampleModule1 = applicationContext(override = true) {
        bean { ComponentA() } bind MyInterface::class
        bean { ComponentA() }
    }

    val sampleModule2 = applicationContext(override = true) {
        bean("A") { ComponentA() as MyInterface }
        bean("B") { ComponentB() as MyInterface }
    }

    val sampleModule3 = applicationContext(override = true) {
        bean { ComponentB() as MyInterface }
        bean { ComponentA() as MyInterface }
    }

    val sampleModule4 = applicationContext(override = true) {
        bean { ComponentB() as MyInterface }
        factory { ComponentA() as MyInterface }
    }

    val noOverrideModule = applicationContext {
        bean { ComponentA() as MyInterface }
        bean { ComponentB() as MyInterface }
    }

    val overrideOnBeanDefinitionModule = applicationContext {
        bean { ComponentA() as MyInterface }
        bean(override = true) { ComponentB() as MyInterface }
    }

    val overrideOnSubContextModule = applicationContext {
        context(name = "SUBCONTEXT", override = true) {
            bean { ComponentA() as MyInterface }
            bean { ComponentB() as MyInterface }
        }
    }

    val noOverrideWithSubContextModule = applicationContext {
        context(name = "SUBCONTEXT") {
            bean { ComponentA() as MyInterface }
        }
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

    @Test
    fun `override without override flag`() {
        try {
            startKoin(listOf(noOverrideModule))
            fail("Should throw BeanOverrideException")
        } catch (e: BeanOverrideException) {
        }
    }

    @Test
    fun `override on bean definition`() {
        startKoin(listOf(overrideOnBeanDefinitionModule))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentB)
    }

    @Test
    fun `override on sub context`() {
        startKoin(listOf(overrideOnSubContextModule))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentB)
    }

    @Test
    @Ignore
    fun `no override with sub context`() {
        startKoin(listOf(noOverrideWithSubContextModule))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentA)

        try {
            loadKoinModules(noOverrideWithSubContextModule)
            fail("Should throw BeanOverrideException")
        } catch (e: BeanOverrideException) {
        }

        releaseContext("SUBCONTEXT")

        try {
            loadKoinModules(noOverrideWithSubContextModule)
        } catch (e: BeanOverrideException) {
            fail("Should NOT throw BeanOverrideException because context was released")
        }
    }
}
