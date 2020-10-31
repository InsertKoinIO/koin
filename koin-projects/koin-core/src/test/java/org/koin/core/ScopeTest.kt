package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.ClosedScopeException
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeTest {

    @Test
    fun `get definition from current scopes type`() {
        val koin = koinApplication {
            printLogger()
            modules(
                    module {
                        scope(named<ClosedScopeAPI.ScopeType>()) {
                            scoped { Simple.ComponentA() }
                        }
                    }
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
                    }
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
                    }
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
                    }
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
                    }
            )
        }.koin

        val scope = koin.createScope(scopeId, scopeKey)
        scope.get<Simple.ComponentB>()
        scope.close()
    }
}
