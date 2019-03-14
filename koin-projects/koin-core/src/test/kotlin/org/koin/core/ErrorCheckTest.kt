package org.koin.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Errors
import org.koin.Simple
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ErrorCheckTest {

    @Test
    fun `unknown definition`() {
        val app = koinApplication {
        }

        try {
            app.koin.get<Simple.ComponentA>()
            fail("should not get instance")
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `unknown linked dependency`() {
        val app = koinApplication {
            modules(module {
                single { Simple.ComponentB(get()) }
            })
        }
        try {
            app.koin.get<Simple.ComponentB>()
            fail("should not get instance")
        } catch (e: InstanceCreationException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `error while creating instance`() {
        val app = koinApplication {
            modules(module {
                single { Errors.Boom() }
            })
        }

        try {
            app.koin.get<Errors.Boom>()
            fail("should got InstanceCreationException")
        } catch (e: InstanceCreationException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `cycle error`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { Errors.CycleA(get()) }
                single { Errors.CycleB(get()) }
            })
        }

        try {
            app.koin.get<Errors.CycleA>()
            fail("should break into cycle")
        } catch (e: StackOverflowError) {
            e.printStackTrace()
        }
    }
}