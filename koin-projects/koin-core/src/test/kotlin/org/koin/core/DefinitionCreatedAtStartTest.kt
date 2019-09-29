package org.koin.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getDefinition

class DefinitionCreatedAtStartTest {

    @Test
    fun `is declared as created at start`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single(createdAtStart = true) { Simple.ComponentA() }
                    }
            )
        }

        val koin = app.koin

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertFalse(defA.instance!!.isCreated(InstanceContext(scope = koin.rootScope)))
    }

    @Test
    fun `is created at start`() {
        val app = startKoin {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single(createdAtStart = true) { Simple.ComponentA() }
                    }
            )
        }

        val koin = app.koin

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertTrue(defA.instance!!.isCreated(InstanceContext(scope = koin.rootScope)))
        stopKoin()
    }

    @Test
    fun `factory is not created at start`() {
        val app = koinApplication {
            modules(
                    module {
                        factory { Simple.ComponentA() }
                    }
            )
        }

        val koin = app.koin

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertFalse(defA.options.isCreatedAtStart)
        assertFalse(defA.instance!!.isCreated(InstanceContext(scope = koin.rootScope)))
        app.close()
    }
}