package org.koin.core

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
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

class OverrideAndCreateatStartTest {
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

    @AfterTest
    fun after(){
        stopKoin()
    }

    @Test
    fun testMe(){
        startKoin {
            printLogger(Level.DEBUG)
            modules(moduleA+moduleB)
        }

        assertTrue(count == 1)
        assertTrue(created == "SomeClassB")
    }
}