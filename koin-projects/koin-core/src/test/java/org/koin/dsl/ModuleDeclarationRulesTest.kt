package org.koin.dsl

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.test.assertDefinitionsCount

class ModuleDeclarationRulesTest {

    @Test
    fun `don't allow redeclaration`() {
        try {
            koinApplication {
                modules(module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentA() }
                })
            }
            fail("should not redeclare")
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `allow redeclaration - different names`() {
        val app = koinApplication {
            printLogger(Level.INFO)
            modules(module {
                single(named("default")) { Simple.ComponentA() }
                single(named("other")) { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }

    @Test
    fun `reject redeclaration - same names`() {
        try {
            koinApplication {
                modules(module {
                    single(named("default")) { Simple.ComponentA() }
                    single(named("default")) { Simple.ComponentB(get()) }
                })
            }
            fail("Should not allow redeclaration for same qualifier")
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `allow redeclaration - default`() {
        val app = koinApplication {
            modules(module {
                single { Simple.ComponentA() }
                single(named("other")) { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }

    @Test
    fun `don't allow redeclaration with different implementation`() {

        try {
            koinApplication {
                modules(
                    module {
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                        single<Simple.ComponentInterface1> { Simple.Component2() }
                    })
            }
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }
}