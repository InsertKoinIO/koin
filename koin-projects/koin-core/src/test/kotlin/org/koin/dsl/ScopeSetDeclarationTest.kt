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

    val scopeKey = named("KEY")

    @Test
    fun `can declare a scoped definition`() {
        val app = koinApplication {
            modules(
                    module {
                        scope(scopeKey) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }
        val def = app.koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)
        assertTrue(def!!.instance is ScopeDefinitionInstance)
        assertTrue(def.getScopeName() == scopeKey)
    }

    @Test
    fun `can declare a scope definition`() {
        val app = koinApplication {
            modules(
                    module {
                        scope(scopeKey) {
                        }
                    }
            )
        }
        val def = app.koin.scopeRegistry.getScopeDefinition(scopeKey.toString())!!
        assertTrue(def.qualifier == scopeKey)
    }

    @Test
    fun `can declare a scoped definition within scope`() {
        val app = koinApplication {
            modules(
                    module {
                        scope(scopeKey) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }
        val def = app.koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)!!
        assertTrue(def.instance is ScopeDefinitionInstance)
        assertTrue(def.getScopeName() == scopeKey)
    }

    @Test
    fun `can't declare 2 scoped same definitions`() {
        try {
            koinApplication {
                modules(
                        module {
                            scope(scopeKey) {
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