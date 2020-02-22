package org.koin.core

import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.getBeanDefinition
import kotlin.test.Test
import kotlin.test.assertFails

class DefinitionThreadingTest {
    @Test
    fun `is declared as created at start`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.ComponentA() }
                    }
            )
        }

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
//        assertFails {
//            defA.copy(pt = SinglePlatformThreading())
//        }
    }

}