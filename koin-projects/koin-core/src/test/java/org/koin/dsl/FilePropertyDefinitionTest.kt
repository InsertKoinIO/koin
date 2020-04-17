package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.NoPropertyFileFoundException
import org.koin.ext.getFloatProperty
import org.koin.ext.getIntProperty

class FilePropertyDefinitionTest {

    @Test
    fun `load and get properties from file`() {
        val key = "KEY"
        val value = "VALUE"

        val koin = koinApplication {
            printLogger()
            fileProperties("/koin.properties")
        }.koin

        val gotValue = koin.getProperty(key)

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

        val gotValue = koin.getProperty(key)

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

        assertEquals("this is a string", koin.getProperty("string.value"))
    }

    @Test
    fun `get int value from file`() {
        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        assertEquals(42, koin.getIntProperty("int.value"))
    }

    @Test
    fun `get float value from file`() {
        val koin = koinApplication {
            printLogger()
            fileProperties()
        }.koin

        assertEquals(42.0f, koin.getFloatProperty("float.value"))
    }
}