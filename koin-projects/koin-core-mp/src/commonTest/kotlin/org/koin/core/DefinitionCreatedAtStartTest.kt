package org.koin.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getBeanDefinition
import org.koin.test.getInstanceFactory

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

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        val instanceFactory = app.getInstanceFactory(Simple.ComponentA::class)!!
        assertFalse(instanceFactory.isCreated())
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

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertTrue(app.getInstanceFactory(Simple.ComponentA::class)!!.isCreated())
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

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertFalse(defA.options.isCreatedAtStart)
        assertFalse(app.getInstanceFactory(Simple.ComponentA::class)!!.isCreated())
        app.close()
    }
}