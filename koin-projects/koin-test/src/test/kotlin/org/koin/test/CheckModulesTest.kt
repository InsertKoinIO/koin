package org.koin.test

import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkModules
import org.koin.test.check.parameterCreatorsOf

class CheckModulesTest {

    @Test
    fun `check a simple module`() {
        koinApplication {
            printLogger(Level.DEBUG)
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
            printLogger(Level.DEBUG)
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
                printLogger(Level.DEBUG)
                modules(
                        module {
                            single { Simple.ComponentB(get()) }
                        }
                )
            }.checkModules()
            fail("should not pass with borken definitions")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check a module with params`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { (s: String) -> Simple.MyString(s) }
                        single(UpperCase) { (s: String) -> Simple.MyString(s.toUpperCase()) }
                    }
            )
        }.checkModules(parameterCreatorsOf {
            create<Simple.MyString> { parametersOf("param") }
            create<Simple.MyString>(UpperCase) { qualifier -> parametersOf(qualifier.toString()) }
        })
    }

    @Test
    fun `check a module with property`() {
        koinApplication {
            printLogger(Level.DEBUG)
            properties(hashMapOf("aValue" to "value"))
            modules(
                    module {
                        single { Simple.MyString(getProperty("aValue")) }
                    }
            )
        }.checkModules()
    }
}