package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PropertyDefinitionTest {

    @Test
    fun `load and get properties`() {
        val key = "KEY"
        val value = "VALUE"
        val values = hashMapOf(key to value)
        val koin = koinApplication {
            properties(values)
        }.koin

        val gotValue = koin.getProperty<String>(key)

        assertEquals(value, gotValue)
    }

    @Test
    fun `default value properties`() {
        val koin = koinApplication {}.koin

        val defaultValue = "defaultValue"
        val gotValue = koin.getProperty<String>("aKey", defaultValue)

        assertEquals(defaultValue, gotValue)
    }

    @Test
    fun `set a property`() {
        val key = "KEY"
        val value = "VALUE"

        val koin = koinApplication { }.koin

        koin.setProperty(key, value)
        val gotValue = koin.getProperty<String>(key)
        assertEquals(value, gotValue)
    }

    @Test
    fun `missing property`() {
        val key = "KEY"
        val koin = koinApplication { }.koin

        val gotValue = koin.getProperty<String>(key)
        assertNull(gotValue)
    }

    @Test
    fun `overwrite a property`() {
        val key = "KEY"
        val value = "VALUE"
        val value2 = "VALUE2"
        val values = hashMapOf(key to value)
        val koin = koinApplication {
            properties(values)
        }.koin

        koin.setProperty(key, value2)
        val gotValue = koin.getProperty<String>(key)
        assertEquals(value2, gotValue)
    }
}