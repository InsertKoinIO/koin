package org.koin.test.core

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinContext
import org.koin.core.scope.getScopePath
import org.koin.core.scope.scope
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest

class ScopeExtTest : AutoCloseKoinTest() {

    @Before
    fun before() {
        startKoin(emptyList())
        (StandAloneContext.koinContext as KoinContext).scopeRegistry.makeScope("org.koin.test.core")
    }

    @Test
    fun `should get KClass scope path`() {
        Assert.assertEquals("org.koin.test.core", getScopePath(ScopeExtTest::class))
    }

    @Test
    fun `should get KClass scope`() {
        Assert.assertEquals("core", ScopeExtTest::class.scope.name)
    }

    @Test
    fun `should get instance KClass scope`() {
        Assert.assertEquals("core", this.scope.name)
    }
}