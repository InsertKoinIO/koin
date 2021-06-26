package org.koin.core

import kotlin.test.assertEquals
import kotlin.test.Test
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
                        single { Simple.MyIntSingle(24) }

                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.MyIntSingle(42) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("scope", named<ClosedScopeAPI.ScopeType>())
        assertEquals(42, scope.get<Simple.MyIntSingle>().id)

        assertEquals(24, koin.get<Simple.MyIntSingle>().id)

    }

}