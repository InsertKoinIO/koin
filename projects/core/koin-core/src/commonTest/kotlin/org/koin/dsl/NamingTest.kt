package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.qualifier.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NamingTest : KoinCoreTest(){

    @Test
    fun `can resolve naming from root`() {
        val scopeName = named("MY_SCOPE")
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single(named("24")) { Simple.MySingle(24) }

                    scope(scopeName) {
                        scoped { Simple.MySingle(42) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(24, scope.get<Simple.MySingle>(named("24")).id)
        assertEquals(42, scope.get<Simple.MySingle>().id)
    }

    @Test
    fun `enum naming`() {
        assertEquals("my_string", named(MyNames.MY_STRING).value)
    }

    @Test
    fun `can resolve enum naming`() {
        val koin = koinApplication {
            modules(
                module {
                    single(named(MyNames.MY_STRING)) { Simple.MySingle(24) }
                },
            )
        }.koin

        assertEquals(24, koin.get<Simple.MySingle>(named(MyNames.MY_STRING)).id)
    }

    @Test
    fun `can resolve scope enum naming`() {
        val scopeName = named(MyNames.MY_SCOPE)
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName) {
                        scoped { Simple.MySingle(42) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(42, scope.get<Simple.MySingle>().id)
    }

    @Test
    fun `can resolve naming with q`() {
        val scopeName = _q("MY_SCOPE")
        val koin = koinApplication {
            modules(
                module {
                    single(_q("24")) { Simple.MySingle(24) }

                    scope(scopeName) {
                        scoped { Simple.MySingle(42) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(24, scope.get<Simple.MySingle>(named("24")).id)
        assertEquals(42, scope.get<Simple.MySingle>().id)
    }

    @Test
    fun same_qualifier_but_different_type() {
        val qualifier = named("qualifier")
        val targetString = "_another_string_"
        val targetInt = 42

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.MyIntFactory(24) }
                    single(qualifier) { Simple.MyIntFactory(targetInt) }
                    single { Simple.MyStringFactory("_a_string_") }
                    single(qualifier) { Simple.MyStringFactory(targetString) }
                },
            )
        }.koin

        assertEquals(targetInt,koin.get<Simple.MyIntFactory>(qualifier).id)
        assertEquals(targetString,koin.get<Simple.MyStringFactory>(qualifier).s)
    }
}

enum class MyNames {
    MY_SCOPE,
    MY_STRING,
}
