package org.koin.test

import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkModules
import org.koin.test.error.BrokenDefinitionException

class CheckModulesTest {

    @Test
    fun `check a simple module`() {
        koinApplication {
            logger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with link`() {
        koinApplication {
            logger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a broken module`() {
        try {
            koinApplication {
                logger(Level.DEBUG)
                modules(
                    module {
                        single { Simple.ComponentB(get()) }
                    }
                )
            }.checkModules()
            fail("should not pass with borken definitions")
        } catch (e: BrokenDefinitionException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check a module with params`() {
        koinApplication {
            logger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with property`() {
        koinApplication {
            logger(Level.DEBUG)
            modules(
                module {
                    single { Simple.MyString(getProperty("aValue")) }
                }
            )
        }.checkModules()
    }
}