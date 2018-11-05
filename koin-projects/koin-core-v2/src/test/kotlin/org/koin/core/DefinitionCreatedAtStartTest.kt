package org.koin.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getDefinition

class DefinitionCreatedAtStartTest {

    @Test
    fun `is declared as created at start`() {
        val app = koinApplication {
            loadModules(
                module {
                    single(isCreatedAtStart = true) { Simple.ComponentA() }
                }
            )
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertFalse(defA.instance.isAlreadyCreated())
    }

    @Test
    fun `is created at start`() {
        val app = koinApplication {
            loadModules(
                module {
                    single(isCreatedAtStart = true) { Simple.ComponentA() }
                }
            )
        }.start()

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertTrue(defA.instance.isAlreadyCreated())
        app.stop()
    }

    @Test
    fun `factory is not created at start`() {
        val app = koinApplication {
            loadModules(
                module {
                    factory{ Simple.ComponentA() }
                }
            )
        }.start()

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertFalse(defA.options.isCreatedAtStart)
        assertFalse(defA.instance.isAlreadyCreated())
        app.stop()
    }

}