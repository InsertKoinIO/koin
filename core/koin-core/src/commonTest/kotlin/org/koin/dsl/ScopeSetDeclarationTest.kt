package org.koin.dsl

import kotlin.test.*
import org.koin.Simple
import org.koin.core.A
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.logger.Level
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier._q
import org.koin.core.qualifier.named
import org.koin.core.scope.ScopeDefinition

@OptIn(KoinInternalApi::class)
class ScopeSetDeclarationTest {

    val scopeKey = named("KEY")

    @Test
    fun `can declare a scoped definition`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeKey) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val def: ScopeDefinition = koin.scopeRegistry.scopeDefinitions.values.first { def -> def.qualifier == scopeKey }
        assertTrue(def.qualifier == scopeKey)

        val scope = koin.createScope("id", scopeKey)
        assertTrue(scope._scopeDefinition == def)
    }

    @Test
    fun `can declare 2 scoped definitions from same type without naming`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named("B")) {
                            scoped { Simple.ComponentA() }
                        }
                        scope(named("A")) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin
        val defA = koin.scopeRegistry.scopeDefinitions.values.first { def -> def.qualifier == _q("A") }
        assertTrue(defA.qualifier == StringQualifier("A"))

        val defB = koin.scopeRegistry.scopeDefinitions.values.first { def -> def.qualifier == _q("B") }
        assertTrue(defB.qualifier == StringQualifier("B"))

        val scopeA = koin.createScope("A", named("A")).get<Simple.ComponentA>()
        val scopeB = koin.createScope("B", named("B")).get<Simple.ComponentA>()
        assertNotEquals(scopeA, scopeB)
    }

    @Test
    fun `can declare a scope definition`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeKey) {
                            scoped { A() }
                        }
                    }
            )
        }.koin
        val def = koin.scopeRegistry.scopeDefinitions.values.first { def -> def.qualifier == scopeKey }
        assertTrue(def.qualifier == scopeKey)
    }

    @Test
    fun `can't declare 2 scoped same definitions`() {
        try {
            startKoin {
                printLogger(Level.DEBUG)
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
        stopKoin()
    }
}