package org.koin.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.BadScopeInstanceException
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.ScopeInstance
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ClosedScopeAPI {

    val scopeName = "MY_SCOPE"

    @Test
    fun `get definition from current scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName) {
                        scoped { Simple.ComponentA() }
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get definition from outside single`() {
        val koin = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                    scope(scopeName) {
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get definition from outside factory`() {
        val koin = koinApplication {
            modules(
                module {
                    factory { Simple.ComponentA() }
                    scope(scopeName) {
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertNotEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `bad mix definition from a scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope("SCOPE_1") {
                        scoped { Simple.ComponentA() }
                    }
                    scope("SCOPE_2") {
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.koin

        val scope2 = koin.createScope("myScope2", "SCOPE_2")
        try {
            scope2.get<Simple.ComponentB>()
            fail()
        } catch (e: BadScopeInstanceException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `mix definition from a scope`() {
        val koin = koinApplication {
            modules(
                module {
                    scope("SCOPE_1") {
                        scoped { Simple.ComponentA() }
                    }
                    scope("SCOPE_2") {
                        scoped { (scope: ScopeInstance) -> Simple.ComponentB(get(scope = scope)) }
                    }
                }
            )
        }.koin

        val scope1 = koin.createScope("myScope1", "SCOPE_1")
        val scope2 = koin.createScope("myScope2", "SCOPE_2")
        val b = scope2.get<Simple.ComponentB> { parametersOf(scope1) }
        val a = scope1.get<Simple.ComponentA>()

        Assert.assertEquals(a, b.a)
    }
}