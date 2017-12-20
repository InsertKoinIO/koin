package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.error.BeanInstanceCreationException
import org.koin.error.MissingPropertyException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getProperty
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class KoinContextTest : AbstractKoinTest() {

    val CircularDeps = applicationContext {
        provide { ComponentA(get()) }
        provide { ComponentB(get()) }
    }


    val SingleModule = applicationContext {
        provide { ComponentA(get()) }
    }


    class ComponentA(val componentB: ComponentB)
    class ComponentB(val componentA: ComponentA)


    @Test
    fun `circular deps injection error`() {
        startKoin(listOf(CircularDeps))

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
        startKoin(listOf(SingleModule))

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
        startKoin(listOf(SingleModule))

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
        startKoin(arrayListOf(SingleModule), true)
        assertNotNull(getProperty(OS_NAME))
    }

    @Test
    fun `assert system properties are not injected by default`() {
        startKoin(arrayListOf(SingleModule))

        try {
            getProperty<String>(OS_NAME)
            fail("should not inject ")
        } catch (ignored: MissingPropertyException) {
        }
    }

    @Test
    fun `assert given properties are injected`() {

        // Should read koin.properties file which contains OS_VERSION definition
        startKoin(arrayListOf(SingleModule), properties = mapOf(GIVEN_PROP to VALUE_ANDROID))
        assertEquals(VALUE_ANDROID, getProperty(GIVEN_PROP))
    }

    @Test
    fun `assert given properties are injected but override koin properties`() {

        // Should read koin.properties file which contains OS_VERSION definition
        startKoin(arrayListOf(SingleModule), properties = mapOf(TEST_KOIN to VALUE_ANDROID))
        assertEquals(VALUE_ANDROID, getProperty(TEST_KOIN))
        assertEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert given properties are injected and overridden by koin properties and system properties`() {

        // Should read koin.properties file which contains OS_VERSION definition
        startKoin(arrayListOf(SingleModule),
                bindSystemProperties = true,
                properties = mapOf(GIVEN_PROP to VALUE_ANDROID, TEST_KOIN to VALUE_ANDROID))

        assertEquals(VALUE_ANDROID, getProperty(GIVEN_PROP))
        assertEquals(VALUE_ANDROID, getProperty(TEST_KOIN))
        assertNotEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert koin properties are injected`() {

        // Should read koin.properties file which contains OS_VERSION definition
        startKoin(arrayListOf(SingleModule))
        assertEquals(VALUE_DONE, getProperty(TEST_KOIN))
        assertEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert system properties are not overridden by koin properties`() {

        startKoin(arrayListOf(SingleModule), true)
        assertNotNull(getProperty(OS_NAME))
        assertEquals(VALUE_DONE, getProperty(TEST_KOIN))
        assertNotEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    companion object {
        const val OS_NAME = "os.name"
        const val OS_VERSION = "os.version"
        const val GIVEN_PROP = "given.prop"
        const val TEST_KOIN = "test.koin"

        const val VALUE_DONE = "done"
        const val VALUE_WEIRD = "weird"
        const val VALUE_ANDROID = "Android"
    }
}