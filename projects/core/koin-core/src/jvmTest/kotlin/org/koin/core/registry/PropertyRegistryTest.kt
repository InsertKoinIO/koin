package org.koin.core.registry

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.KoinCoreTest
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.environmentProperties
import java.util.*

class PropertyRegistryTest : KoinCoreTest() {

    @Before
    fun setupEdgeCaseProperties() {
        val map = Properties()

        // load existing properties
        map.putAll(System.getProperties())

        map[Any::class.java] = Any()
        map["koin.test.kotlin.any.class"] = Any::class
        map[ArrayList::class.java] = ArrayList<Any?>()

        System.setProperties(map)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun arbitraryObjectProperties() {
        val koin = startKoin {
            printLogger(Level.INFO)

            environmentProperties()
        }.koin

        Assert.assertEquals(Any::class, koin.propertyRegistry.getProperty<Any?>("koin.test.kotlin.any.class"))
    }
}
