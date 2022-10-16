package org.koin.test.android.ext.android

import android.content.ComponentCallbacks
import android.content.res.Configuration
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.android.getKoinScope
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.createScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.scope.Scope
import org.koin.test.KoinTest

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(KoinInternalApi::class)
class AndroidKoinScopeExtTest : KoinTest {

    @Before
    fun before() {
        startKoin { }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `GIVEN AndroidScopeComponent WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = object : ComponentCallbacks, AndroidScopeComponent {
            override var scope: Scope? = createScope("AndroidScopeComponent")
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = Unit
        }

        // WHEN
        val result = input.getKoinScope()

        // THEN
        Assert.assertEquals(input.scope, result)
    }

    @Test
    fun `GIVEN KoinScopeComponent WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = object : ComponentCallbacks, KoinScopeComponent {
            override var scope: Scope = createScope("KoinScopeComponent")
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = Unit
        }

        // WHEN
        val result = input.getKoinScope()

        // THEN
        Assert.assertEquals(input.scope, result)
    }

    @Test
    fun `GIVEN KoinComponent WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = object : ComponentCallbacks, KoinComponent {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = Unit
        }

        // WHEN
        val result = input.getKoinScope()

        // THEN
        Assert.assertEquals(input.getKoin().scopeRegistry.rootScope, result)
    }

    @Test
    fun `GIVEN ComponentCallbacks WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = Unit
        }

        // WHEN
        val result = input.getKoinScope()

        // THEN
        Assert.assertEquals(GlobalContext.get().scopeRegistry.rootScope, result)
    }

}
