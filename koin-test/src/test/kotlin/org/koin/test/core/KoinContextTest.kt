package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.error.BeanInstanceCreationException
import org.koin.error.MissingPropertyException
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.get
import org.koin.test.getProperty

class KoinContextTest : KoinTest {

    class CircularDeps() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA(get()) }
            provide { ComponentB(get()) }
        }
    }

    class SingleModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA(get()) }
        }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `circular deps injection error`() {
        startContext(listOf(CircularDeps()))

        assertDefinitions(2)
        assertRemainingInstances(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
        assertContexts(1)
    }

    @Test
    fun `safe missing bean`() {
        startContext(listOf(SingleModule()))

        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
    }

    @Test
    fun `unsafe missing bean`() {
        startContext(listOf(SingleModule()))

        assertDefinitions(1)
        assertRemainingInstances(0)
        try {
            get<ComponentA>()
            fail("should not inject ")
        } catch (e: BeanInstanceCreationException) {
            System.err.println(e)
        }
        assertRemainingInstances(0)
    }

    @Test
    fun `assert system properties are well injected if specified as so`() {
        startContext(arrayListOf(SingleModule()), true)
        assertNotNull(getProperty("os.name"))
    }

    @Test
    fun `assert system properties are not injected by default`() {
        startContext(arrayListOf(SingleModule()))

        try {
            getProperty<String>("os.name")
            fail("should not inject ")
        } catch (ignored: MissingPropertyException) { }
    }

    @Test
    fun `assert koin properties are injected`() {

        // Should read koin.properties file which contains "os.version" definition
        startContext(arrayListOf(SingleModule()))
        assertEquals("done", getProperty("test.koin"))
        assertEquals("weird", getProperty("os.version"))
    }

    @Test
    fun `assert system properties overrides koin properties`() {

        startContext(arrayListOf(SingleModule()), true)
        assertNotNull(getProperty("os.name"))
        assertEquals("done", getProperty("test.koin"))
        assertNotEquals("weird", getProperty("os.version"))
    }
}