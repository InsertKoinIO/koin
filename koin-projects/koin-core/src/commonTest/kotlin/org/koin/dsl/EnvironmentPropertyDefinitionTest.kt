package org.koin.dsl

import org.koin.core.logger.Level
import org.koin.core.mp.KoinMultiPlatform
import kotlin.test.Test
import kotlin.test.assertEquals

class EnvironmentPropertyDefinitionTest {

    @Test
    fun `load and get properties from environment`() {
        val sysProperties = KoinMultiPlatform.getSystemProperties()
        val aPropertyKey: String = sysProperties.keys.first()
        val aPropertyValue = sysProperties[aPropertyKey]

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            environmentProperties()
        }.koin

        val foundValue = koin.getProperty<String>(aPropertyKey)
        assertEquals(aPropertyValue, foundValue)
    }
}
