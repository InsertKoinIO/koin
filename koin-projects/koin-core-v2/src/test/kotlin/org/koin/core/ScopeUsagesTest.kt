package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.scope.Scope
import org.koin.dsl.SCOPE_ID
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeUsagesTest {

    @Test
    fun `create a scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin

        val scope: Scope = koin.createScope(SCOPE_ID)
        assertEquals(SCOPE_ID, scope.id)
    }

    @Test
    fun `can't resolve a component from a non existing scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin

        try {
            koin.get<Simple.ComponentA>()
            fail("Should not be able to resolve a scoped component without creating its scope")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}