package org.koin.dsl

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.instance.ScopeDefinitionInstance
import org.koin.core.qualifier.named
import org.koin.core.scope.getScopeName

class ScopeSetDeclarationTest {

    val key = named("KEY")

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
        assertTrue(def!!.instance is ScopeDefinitionInstance)
        assertTrue(def.getScopeName() == null)
    }

    @Test
    fun `can declare a scope definition`() {
        val app = koinApplication {
            modules(
                    module {
                        scope(key) {
                        }
                    }
            )
        }
        val def = app.koin.scopeRegistry.getScopeDefinition(key.toString())!!
        assertTrue(def.qualifier == key)
    }

    @Test
    fun `can declare a scoped definition within scope`() {
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
        assertTrue(def.instance is ScopeDefinitionInstance)
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
                            scope(named("scope_name")) {
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