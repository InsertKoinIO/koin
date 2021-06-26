package org.koin.dsl

import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.qualifier.*

class NamingTest {

    @Test
    fun `can resolve naming from root`() {
        val scopeName = named("MY_SCOPE")
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {

                single(named("24")) { Simple.MyIntSingle(24) }

                scope(scopeName) {
                    scoped { Simple.MyIntSingle(42) }
                }
            })
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(24, scope.get<Simple.MyIntSingle>(named("24")).id)
        assertEquals(42, scope.get<Simple.MyIntSingle>().id)
    }

    @Test
    fun `enum naming`() {
        assertEquals("my_string",named(MyNames.MY_STRING).value)
    }

    @Test
    fun `can resolve enum naming`() {
        val koin = koinApplication {
            modules(module {
                single(named(MyNames.MY_STRING)) { Simple.MyIntSingle(24) }

            })
        }.koin

        assertEquals(24, koin.get<Simple.MyIntSingle>(named(MyNames.MY_STRING)).id)
    }

    @Test
    fun `can resolve scope enum naming`() {
        val scopeName = named(MyNames.MY_SCOPE)
        val koin = koinApplication {
            modules(module {

                scope(scopeName) {
                    scoped { Simple.MyIntSingle(42) }
                }
            })
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(42, scope.get<Simple.MyIntSingle>().id)
    }

    @Test
    fun `can resolve naming with q`() {
        val scopeName = _q("MY_SCOPE")
        val koin = koinApplication {
            modules(module {

                single(_q("24")) { Simple.MyIntSingle(24) }

                scope(scopeName) {
                    scoped { Simple.MyIntSingle(42) }
                }
            })
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(24, scope.get<Simple.MyIntSingle>(named("24")).id)
        assertEquals(42, scope.get<Simple.MyIntSingle>().id)
    }
}

enum class MyNames {
    MY_SCOPE,
    MY_STRING
}