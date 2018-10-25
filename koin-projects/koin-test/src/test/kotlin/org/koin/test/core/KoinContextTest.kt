package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.KoinProperties
import org.koin.dsl.module.module
import org.koin.error.BeanInstanceCreationException
import org.koin.error.MissingPropertyException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getProperty
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class KoinContextTest : AutoCloseKoinTest() {

    val CircularDeps = module {
        single { ComponentA(get()) }
        single { ComponentB(get()) }
    }


    val SingleModule = module {
        single { ComponentA(get()) }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB(val componentA: ComponentA)


    @Test
    fun `circular deps injection error`() {
        startKoin(listOf(CircularDeps))

        assertDefinitions(2)
        assertRemainingInstanceHolders(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }

        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }

    @Test
    fun `safe missing bean`() {
        startKoin(listOf(SingleModule))

        assertDefinitions(1)
        assertRemainingInstanceHolders(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }

        assertRemainingInstanceHolders(1)
    }

    @Test
    fun `unsafe missing bean`() {
        startKoin(listOf(SingleModule))

        assertDefinitions(1)
        assertRemainingInstanceHolders(0)
        try {
            get<ComponentA>()
            fail("should not inject ")
        } catch (e: BeanInstanceCreationException) {
            System.err.println(e)
        }
        assertRemainingInstanceHolders(1)
    }

    @Test
    fun `assert system properties are well injected if specified as so`() {
        startKoin(arrayListOf(SingleModule), KoinProperties(true))
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

        // Should read koin.properties file which contains OS_VERSION beanDefinition
        startKoin(arrayListOf(SingleModule), KoinProperties(extraProperties = mapOf(GIVEN_PROP to VALUE_ANDROID)))
        assertEquals(VALUE_ANDROID, getProperty(GIVEN_PROP))
    }

    @Test
    fun `assert given properties are injected but override koin properties`() {

        // Should read koin.properties file which contains OS_VERSION beanDefinition
        startKoin(arrayListOf(SingleModule), KoinProperties(extraProperties = mapOf(TEST_KOIN to VALUE_ANDROID),useKoinPropertiesFile = true))
        assertEquals(VALUE_ANDROID, getProperty(TEST_KOIN))
        assertEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert given properties are injected and overridden by koin properties and system properties`() {

        // Should read koin.properties file which contains OS_VERSION beanDefinition
        startKoin(
            listOf(),
            KoinProperties(
            useEnvironmentProperties = true,
            extraProperties = mapOf(GIVEN_PROP to VALUE_ANDROID, TEST_KOIN to VALUE_ANDROID))
        )

        assertEquals(VALUE_ANDROID, getProperty(GIVEN_PROP))
        assertEquals(VALUE_ANDROID, getProperty(TEST_KOIN))
        assertNotEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert koin properties are injected`() {

        // Should read koin.properties file which contains OS_VERSION beanDefinition
        startKoin(listOf(), KoinProperties(false, true))
        assertEquals(VALUE_DONE, getProperty(TEST_KOIN))
        assertEquals(VALUE_WEIRD, getProperty(OS_VERSION))
    }

    @Test
    fun `assert system properties are not overridden by koin properties`() {

        startKoin(listOf(), KoinProperties(true, true))
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