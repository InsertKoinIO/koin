package org.koin.dsl

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.instance.ScopeInstance
import org.koin.core.scope.getScopeKey

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
        assertTrue(def!!.instance is ScopeInstance)
        assertTrue(def.getScopeKey() == null)
    }

    @Test
    fun `can declare a scoped definition with scopeKey`() {
        val key = "KEY"
        val app = koinApplication {
            modules(
                module {
                    scoped(key) { Simple.ComponentA() }
                }
            )
        }
        val def = app.koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)
        assertTrue(def!!.instance is ScopeInstance)
        assertTrue(def.getScopeKey() == key)
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
                        scoped { Simple.ComponentA() }
                        scoped("key") { Simple.ComponentA() }
                    }
                )
            }
            fail()
        } catch (e: DefinitionOverrideException) {
            e.printStackTrace()
        }
    }
}