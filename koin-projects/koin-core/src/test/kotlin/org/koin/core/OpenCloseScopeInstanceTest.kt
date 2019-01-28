package org.koin.core

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.BadScopeInstanceException
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class OpenCloseScopeInstanceTest {

    @Test
    fun `get definition from a scope`() {
        val scopeName = "MY_SCOPE"
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeName) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentA>())
    }

    @Test
    fun `can't get definition from a opened scope`() {
        val scopeName = "MY_SCOPE"
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeName) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope")
        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: BadScopeInstanceException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `get definition from scope and out of scope`() {
        val scopeName = "MY_SCOPE"
        val koin = koinApplication {
            modules(
                    module {
                        scoped { Simple.ComponentA() }
                        scope(scopeName) {
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        val a = scope.get<Simple.ComponentA>()
        val b = scope.get<Simple.ComponentB>()

        assertEquals(a, b.a)
    }

    @Test
    fun `can't get definition from wrong scope`() {
        val scope1Name = "SCOPE_1"
        val koin = koinApplication {
            modules(
                    module {
                        scope(scope1Name) {
                        }
                        scope("SCOPE_2") {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", scope1Name)
        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: BadScopeInstanceException) {
            e.printStackTrace()
        }
    }
}