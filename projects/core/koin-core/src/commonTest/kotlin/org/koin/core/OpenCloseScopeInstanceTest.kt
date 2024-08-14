package org.koin.core

import org.koin.Simple
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class OpenCloseScopeInstanceTest {

    val scopeName = named("MY_SCOPE")

    @Test
    fun `get definition from a scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentA>())
    }

    @Test
    fun `can't get definition from another scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        try {
            val scope = koin.createScope("myScope", named("otherName"))
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `get definition from scope and out of scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName) {
                        scoped { Simple.ComponentA() }
                        scoped { Simple.ComponentB(get()) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        val a = scope.get<Simple.ComponentA>()
        val b = scope.get<Simple.ComponentB>()

        assertEquals(a, b.a)
    }

    @Test
    fun `can't get definition from wrong scope`() {
        val scope1Name = named("SCOPE_1")
        val koin = koinApplication {
            modules(
                module {
                    scope(scope1Name) {
                        scoped { B() }
                    }
                    scope(named("SCOPE_2")) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("myScope", scope1Name)
        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }
}
