package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class GlobalToScopeTest {

    @Test
    fun `can't get scoped dependency without scope`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        try {
            koin.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't get scoped dependency without scope from single`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.ComponentB(get()) }

                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        try {
            koin.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `get scoped dependency without scope from single`() {

        val scopeId = "MY_SCOPE_ID"

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.ComponentB(getScope(scopeId).get()) }

                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope(scopeId, named<ClosedScopeAPI.ScopeType>())
        assertEquals(koin.get<Simple.ComponentB>().a, scope.get<Simple.ComponentA>())
    }
}