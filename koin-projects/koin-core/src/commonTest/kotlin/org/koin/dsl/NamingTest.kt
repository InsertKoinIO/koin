package org.koin.dsl

import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.qualifier.*

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

    @Test
    fun `can resolve naming with q`() {
        val scopeName = _q("MY_SCOPE")
        val koin = koinApplication {
            modules(module {

                single(_q("24")) { Simple.MySingle(24) }

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