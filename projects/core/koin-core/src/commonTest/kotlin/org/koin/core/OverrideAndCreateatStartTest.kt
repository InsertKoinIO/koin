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
import org.koin.mp.KoinPlatform.getKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
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
class SomeClassC : SomeClassInterface {
    init {
        println("SomeClassC created")
        count++
        created = "SomeClassC"
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

    val moduleC = module {
        single<SomeClassInterface> {
            SomeClassC()
        }
    }

    @BeforeTest
    fun setup(){
        count = 0
        created = ""
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

        getKoin().instanceRegistry.instances.firstNotNullOf { (k: IndexKey,v: InstanceFactory<*>) ->
            assertEquals(k, moduleB.mappings.keys.first())
            assertEquals(v, moduleB.mappings.values.first())
        }
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun testDefinitionOverride_no_created_at_start() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(moduleA + moduleC)
        }

        assertTrue(count == 0)
        val result = getKoin().get<SomeClassInterface>()
        println("=> $result")
        assertTrue(count == 1)
        assertTrue(created == "SomeClassC")

        getKoin().instanceRegistry.instances.firstNotNullOf { (k: IndexKey,v: InstanceFactory<*>) ->
            assertEquals(k, moduleC.mappings.keys.first())
            assertEquals(v, moduleC.mappings.values.first())
        }
    }
}
