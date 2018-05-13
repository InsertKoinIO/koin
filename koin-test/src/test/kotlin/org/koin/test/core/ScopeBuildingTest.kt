package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeRegistry

class ScopeBuildingTest {

    lateinit var scopeRegistry: ScopeRegistry

    @Before
    fun before() {
        scopeRegistry = ScopeRegistry()
    }

    @Test
    fun `should create Scope`() {
        val scope = scopeRegistry.makeScope("org.koin")

        Assert.assertEquals(3, scopeRegistry.scopes.size)
        Assert.assertEquals("koin", scope.name)
    }

    @Test
    fun `should create several hierachical Scopes`() {
        scopeRegistry.makeScope("org.koin")
        val scope = scopeRegistry.makeScope("org.koin.test")

        Assert.assertEquals(4, scopeRegistry.scopes.size)
        Assert.assertEquals("test", scope.name)
    }

    @Test
    fun `should get scopes`() {
        scopeRegistry.makeScope("org.koin.test")

        Assert.assertEquals("org", scopeRegistry.getScope("org").name)
        Assert.assertEquals("koin", scopeRegistry.getScope("org.koin").name)
        Assert.assertEquals("test", scopeRegistry.getScope("org.koin.test").name)
        Assert.assertEquals(Scope.ROOT, scopeRegistry.getScope("").name)
    }

    @Test
    fun `should get all scopes from`() {
        scopeRegistry.makeScope("org.koin.test")

        Assert.assertEquals(3, scopeRegistry.getAllScopesFrom("org").size)
        Assert.assertEquals(2, scopeRegistry.getAllScopesFrom("org.koin").size)
        Assert.assertEquals(1, scopeRegistry.getAllScopesFrom("org.koin.test").size)
    }

    @Test
    fun `should not get all scopes from`() {
        scopeRegistry.makeScope("org.koin")

        try {
            scopeRegistry.getAllScopesFrom("or")
            fail()
        } catch (e: Exception) {
        }
        try {
            scopeRegistry.getAllScopesFrom("org.koi")
            fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun `should get all multi scopes`() {
        scopeRegistry.makeScope("org.koin.test")
        scopeRegistry.makeScope("org.koin.core")

        Assert.assertEquals(4, scopeRegistry.getAllScopesFrom("org").size)
        Assert.assertEquals(3, scopeRegistry.getAllScopesFrom("org.koin").size)
        Assert.assertEquals(1, scopeRegistry.getAllScopesFrom("org.koin.test").size)
        Assert.assertEquals(1, scopeRegistry.getAllScopesFrom("org.koin.core").size)
    }
}