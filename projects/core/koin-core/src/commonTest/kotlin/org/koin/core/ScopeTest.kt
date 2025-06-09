package org.koin.core

import org.koin.Simple
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.getOrCreateScope
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.error.ClosedScopeException
import org.koin.core.logger.Level
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.*

class ScopeTest {

    private class A
    private class B

    @Test
    fun scope_component() {
        abstract class SuperClass : KoinScopeComponent {
            override val scope: Scope by getOrCreateScope()
        }

        class SubClass : SuperClass() {
            val a: A by inject()
            val b: B by inject()
        }

        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    singleOf(::A)
                    scope<SubClass> {
                        scopedOf(::B)
                    }
                },
            )
        }

        with(SubClass()) {
            assertTrue { a is A }
            assertTrue { b is B }
        }
    }

    @Test
    fun `get definition from current scopes type`() {
        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    scope(named<ClosedScopeAPI.ScopeType>()) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        val scope = koin.createScope("scope1", named<ClosedScopeAPI.ScopeType>())
        assertNotNull(scope.getOrNull<Simple.ComponentA>())
        assertEquals(scope.getOrNull<Simple.ComponentA>(), scope.getOrNull<Simple.ComponentA>())
        scope.close()
        assertTrue(scope.closed)
        assertNull(scope.getOrNull<Simple.ComponentA>())
        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: ClosedScopeException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `call linked scope factory definition only once if not found in current scope`() {
        var factoryCallCounter = 0

        val koin = koinApplication {
            printLogger()
            modules(
                module {
                    factory {
                        factoryCallCounter++
                        Simple.ComponentA()
                    }
                    scope(named<ClosedScopeAPI.ScopeType>()) {
                        scoped { B() }
                    }
                },
            )
        }
            .koin

        val scope = koin.createScope("scope1", named<ClosedScopeAPI.ScopeType>())
        scope.get<Simple.ComponentA>()
        assertEquals(factoryCallCounter, 1)
    }

    @Test
    fun `recreate a scope`() {
        val baseUrl = "base_url"
        val baseUrl2 = "base_url"
        val baseUrlKey = named("BASE_URL_KEY")

        val scopeId = "user_scope"
        val scopeKey = named("KEY")

        val koin = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        val scope = koin.createScope(scopeId, scopeKey)
        scope.declare(baseUrl, baseUrlKey)
        assertEquals(baseUrl, scope.get<String>(baseUrlKey))

        scope.close()

        val scope2 = koin.createScope(scopeId, scopeKey)
        scope2.declare(baseUrl2, baseUrlKey)

        assertEquals(baseUrl2, scope2.get<String>(baseUrlKey))
    }

    @Test
    fun `can create empty scope`() {
        val baseUrl = "base_url"
        val baseUrlKey = named("BASE_URL_KEY")

        val scopeId = "user_scope"
        val scopeKey = named("KEY")

        val koin = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                    }
                },
            )
        }.koin

        val scope = koin.createScope(scopeId, scopeKey)
        scope.declare(baseUrl, baseUrlKey)
        assertEquals(baseUrl, scope.get<String>(baseUrlKey))
        scope.close()
    }

    @Test
    fun `redeclare scope`() {
        val scopeId = "user_scope"
        val scopeKey = named("KEY")

        val koin = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        scoped { Simple.ComponentA() }
                    }
                    scope(scopeKey) {
                        scoped { Simple.ComponentB(get()) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope(scopeId, scopeKey)
        scope.get<Simple.ComponentB>()
        scope.close()
    }

    @Test
    fun scope_param() {
        val scopeId = "user_scope"
        val scopeKey = named("KEY")
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        scoped { (id: Int) -> Simple.MySingle(id) }
                    }
                },
            )
        }.koin

        val scope = koin.createScope(scopeId, scopeKey)
        val id = 42
        val single = scope.get<Simple.MySingle> { parametersOf(id) }
        assertEquals(id, single.id)
        scope.close()
    }

    @Test
    fun scope_regression_test(){
        startKoin {
            modules(
                module {
                    single<Unit> {}
                    scope(named("one")) {}
                    scope(named("two")) {
                        scoped<String> {
                            get<Unit>().toString() // gets Unit from the root scope
                        }
                    }
                },
            )
        }.run {
            val one = koin.createScope("one", named("one"))
            val two = koin.createScope("two", named("two")).apply {
                unlink(getScope("_root_"))
                linkTo(one)
            }
            // in 4.1.0-Beta11 -> prints "kotlin.Unit"
            // in 4.1.0-RC1 -> throws NoDefinitionFoundException
            assertEquals(Unit.toString(),two.get<String>())
        }
    }
}
