package org.koin.core

import org.koin.KoinCoreTest
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface SomeClassInterface
var created = ""
var count = 0

class SomeClassA : SomeClassInterface {
    init {
        println("SomeClassA created")
        count++
        created = "SomeClassA"
    }
}
class SomeClassB : SomeClassInterface {
    init {
        println("SomeClassB created")
        count++
        created = "SomeClassB"
    }
}

class OverrideAndCreateatStartTest : KoinCoreTest(){
    val moduleA = module {
        single<SomeClassInterface>(createdAtStart = true) {
            SomeClassA()
        }
    }

    val moduleB = module {
        single<SomeClassInterface>(createdAtStart = true) {
            SomeClassB()
        }
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun testDefinitionOverride() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(moduleA + moduleB)
        }

        assertTrue(count == 1)
        assertTrue(created == "SomeClassB")
        KoinPlatform.getKoin().instanceRegistry.instances.firstNotNullOf { (k: IndexKey,v: InstanceFactory<*>) ->
            assertEquals(k, moduleB.mappings.keys.first())
            assertEquals(v, moduleB.mappings.values.first())
        }
    }
}
