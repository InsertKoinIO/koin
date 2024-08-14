// package org.koin.dsl
//
// import kotlin.test.assertEquals
// import kotlin.test.Test
// import org.koin.core.logger.Level
//
// class EnvironmentPropertyDefinitionTest {
//
//    @Test
//    fun `load and get properties from environment`() {
//        val sysProperties = System.getProperties()
//        val aPropertyKey: String = sysProperties.keys.first() as String
//        val aPropertyValue = sysProperties.getProperty(aPropertyKey)
//
//        val koin = koinApplication {
//            printLogger(Level.DEBUG)
//            environmentProperties()
//        }.koin
//
//        val foundValue = koin.getProperty<String>(aPropertyKey)
//        assertEquals(aPropertyValue, foundValue)
//    }
// }
