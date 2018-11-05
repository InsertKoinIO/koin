package org.koin.dsl

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionAlreadyExistsException
import org.koin.core.error.DefinitionOverrideException
import org.koin.test.assertDefinitionsCount

class ModuleDeclarationRulesTest {

    @Test
    fun `don't allow redeclaration`() {
        try {
            koinApplication {
                loadModules(module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentA() }
                })
            }
            fail("should not redeclare")
        } catch (e: DefinitionAlreadyExistsException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `allow redeclaration - different names`() {
        val app = koinApplication {
            loadModules(module {
                single("default") { Simple.ComponentA() }
                single("other") { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }

    @Test
    fun `reject redeclaration - same names`() {
        try {
            koinApplication {
                loadModules(module {
                    single("default") { Simple.ComponentA() }
                    single("default") { Simple.ComponentB(get()) }
                })
            }
            fail("Should not allow redeclaration for same name")
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `allow redeclaration - default`() {
        val app = koinApplication {
            loadModules(module {
                single { Simple.ComponentA() }
                single("other") { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }

    @Test
    fun `don't allow redeclaration with different implementation`() {

        try {
            koinApplication {
                loadModules(
                    module {
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                        single<Simple.ComponentInterface1> { Simple.Component2() }
                    })
            }
        } catch (e: DefinitionAlreadyExistsException) {
            e.printStackTrace()
        }
    }
}