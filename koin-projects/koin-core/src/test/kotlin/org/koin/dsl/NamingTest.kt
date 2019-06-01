package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named

class NamingTest {

    @Test
    fun `can resolve naming from root`() {
        val scopeName = named("MY_SCOPE")
        val koin = koinApplication {
            modules(module {

                single(named("24")) { Simple.MySingle(24) }

                scope(scopeName) {
                    scoped { Simple.MySingle(42) }
                }
            })
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(24, scope.get<Simple.MySingle>(named("24")).id)
        assertEquals(42, scope.get<Simple.MySingle>().id)
    }
}