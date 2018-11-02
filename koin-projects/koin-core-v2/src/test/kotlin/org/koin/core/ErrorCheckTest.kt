package org.koin.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Errors
import org.koin.Simple
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoDefinitionFoundException
import org.koin.dsl.koin
import org.koin.dsl.module

class ErrorCheckTest {

    @Test
    fun `unknown definition`() {
        val app = koin {
        }

        try {
            app.koin.get<Simple.ComponentA>()
            fail("should not get undeclared component")
        } catch (e: NoDefinitionFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `error while creating instance`() {
        val app = koin {
            loadModules(module {
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
        val app = koin {
            loadModules(module {
                single { Errors.CycleA(get()) }
                single { Errors.CycleB(get()) }
            })
        }

        try {
            app.koin.get<Errors.CycleA>()
            fail("should break into cycle")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}