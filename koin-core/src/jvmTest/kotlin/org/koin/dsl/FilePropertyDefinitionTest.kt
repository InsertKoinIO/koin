package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.NoPropertyFileFoundException

class FilePropertyDefinitionTest {

    @Test
    fun `load and get properties from file`() {
        val key = "KEY"
        val value = "VALUE"

        val koin = koinApplication {
            printLogger()
            fileProperties("/koin.properties")
        }.koin

        val gotValue = koin.getProperty<String>(key)

        assertEquals(value, gotValue)
    }

    @Test
    fun `load and get properties from default file`() {
        val key = "KEY"
        val value = "VALUE"

        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        val gotValue = koin.getProperty<String>(key)

        assertEquals(value, gotValue)
    }

    @Test
    fun `load and get properties from bad file`() {
        try {
            koinApplication {
                fileProperties("koin_bad.properties")
            }
            fail("should not find any koin_bad.properties file")
        } catch (e: NoPropertyFileFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `get string value from file`() {
        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        assertEquals("this is a string", koin.getProperty<String>("string.value"))
    }

    @Test
    fun `get int value from file`() {
        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        assertEquals(42, koin.getProperty<Int>("int.value"))
    }

    @Test
    fun `get float value from file`() {
        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        assertEquals(42.0f, koin.getProperty<Int>("float.value"))
    }
}