package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.scope.getScopeId
import org.koin.test.getDefinition

const val SCOPE_ID = "myScope"

class ScopeDeclarationTest {

    @Test
    fun `declare scoped definition`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }

        val defA = app.getDefinition(Simple.ComponentA::class)!!
        assertEquals(SCOPE_ID, defA.getScopeId())
    }

    @Test
    fun `declare scope group declaration`() {
        val app = koinApplication {
            loadModules(module {
                withScope(SCOPE_ID) {
                    scoped { Simple.ComponentA() }
                    scoped { Simple.ComponentB(get()) }
                }
            })
        }

        val defA = app.getDefinition(Simple.ComponentA::class)!!
        assertEquals(SCOPE_ID, defA.getScopeId())
        val defB = app.getDefinition(Simple.ComponentB::class)!!
        assertEquals(SCOPE_ID, defB.getScopeId())
    }
}