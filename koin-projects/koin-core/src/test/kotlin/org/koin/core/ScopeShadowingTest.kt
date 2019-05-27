package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeShadowingTest {

    @Test
    fun `can't get scoped dependency without scope from single`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.MySingle(24) }

                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.MySingle(42) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("scope", named<ClosedScopeAPI.ScopeType>())
        assertEquals(42, scope.get<Simple.MySingle>().id)

        assertEquals(24, koin.get<Simple.MySingle>().id)

    }

}