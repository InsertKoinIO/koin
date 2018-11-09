package org.koin.dsl

import org.junit.Assert.assertNotNull
import org.junit.Test

class EnvironmentPropertyDefinitionTest {

    @Test
    fun `load and get properties from environment`() {

        val koin = koinApplication {
            useLogger()
            loadEnvironmentProperties()
        }.koin

        val osName = koin.getProperty<String>("os.name")
        val osVersion = koin.getProperty<Float>("os.version")
        println("os data from env : $osName $osVersion")
        
        assertNotNull(osName)
        assertNotNull(osVersion)
    }
}