package org.koin.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ClosedScopeAPI {

    class ScopeType

    val scopeName = "MY_SCOPE"

    @Test
    fun `get definition from current scope type`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named<ScopeType>()) {
                            scoped { Simple.ComponentA() }
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", named<ScopeType>())
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get definition from current scope`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named(scopeName)) {
                            scoped { Simple.ComponentA() }
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", named(scopeName))
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get definition from outside single`() {
        val koin = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                        scope(named(scopeName)) {
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", named(scopeName))
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get definition from outside factory`() {
        val koin = koinApplication {
            modules(
                    module {
                        factory { Simple.ComponentA() }
                        scope(named(scopeName)) {
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", named(scopeName))
        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
        Assert.assertNotEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `bad mix definition from a scope`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named("SCOPE_1")) {
                            scoped { Simple.ComponentA() }
                        }
                        scope(named("SCOPE_2")) {
                            scoped { Simple.ComponentB(get()) }
                        }
                    }
            )
        }.koin

        val scope2 = koin.createScope("myScope2", named("SCOPE_2"))
        try {
            scope2.get<Simple.ComponentB>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `mix definition from a scope`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named("SCOPE_1")) {
                            scoped { Simple.ComponentA() }
                        }
                        scope(named("SCOPE_2")) {
                            scoped { (scope: Scope) -> Simple.ComponentB(scope.get()) }
                        }
                    }
            )
        }.koin

        val scope1 = koin.createScope("myScope1", named("SCOPE_1"))
        val scope2 = koin.createScope("myScope2", named("SCOPE_2"))
        val b = scope2.get<Simple.ComponentB> { parametersOf(scope1) }
        val a = scope1.get<Simple.ComponentA>()

        Assert.assertEquals(a, b.a)
    }

    @Test
    fun `definition params for scoped definitions`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named("SCOPE_1")) {
                            scoped { (i: Int) -> Simple.MySingle(i) }
                        }
                    }
            )
        }.koin

        val scope1 = koin.createScope("myScope1", named("SCOPE_1"))
        val parameters = 42
        val a = scope1.get<Simple.MySingle> { parametersOf(parameters) }
        Assert.assertEquals(parameters, a.id)
    }
}