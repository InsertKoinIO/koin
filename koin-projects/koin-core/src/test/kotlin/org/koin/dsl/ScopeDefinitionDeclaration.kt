package org.koin.dsl

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.instance.ScopedInstance
import org.koin.core.scope.getScopeName

class ScopeDefinitionDeclaration {

    @Test
    fun `can declare a scoped definition`() {
        val app = koinApplication {
            modules(
                module {
                    scoped { Simple.ComponentA() }
                }
            )
        }
        val def = app.koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)
        assertTrue(def!!.instance is ScopedInstance)
        assertTrue(def.getScopeName() == null)
    }

    @Test
    fun `can declare a scope definition`() {
        val key = "KEY"
        val app = koinApplication {
            modules(
                module {
                    scope(key) {
                    }
                }
            )
        }
        val def = app.koin.scopeRegistry.getScopeDefinition(key)!!
        assertTrue(def.scopeName == key)
    }

    @Test
    fun `can declare a scoped definition within scope`() {
        val key = "KEY"
        val app = koinApplication {
            modules(
                module {
                    scope(key) {
                        scoped { Simple.ComponentA() }
                    }
                }
            )
        }
        val def = app.koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)!!
        assertTrue(def.instance is ScopedInstance)
        assertTrue(def.getScopeName() == key)
    }

    @Test
    fun `can't declare 2 scoped same definitions`() {
        try {
            koinApplication {
                modules(
                    module {
                        scoped { Simple.ComponentA() }
                        scoped { Simple.ComponentA() }
                    }
                )
            }
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't declare 2 scoped same definitions with key`() {
        try {
            koinApplication {
                modules(
                    module {
                        scope("scope_name") {
                            scoped { Simple.ComponentA() }
                            scoped { Simple.ComponentA() }
                        }
                    }
                )
            }
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }
}