package org.koin.test.android.ext.android

import android.content.ComponentCallbacks
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.android.getKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.android.helper.Helper.androidScopeComponent
import org.koin.test.android.helper.Helper.componentCallbacks
import org.koin.test.android.helper.Helper.koinComponent
import org.koin.test.android.helper.Helper.koinScopeComponent

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
        val input = androidScopeComponent

        // WHEN
        val result = (input as ComponentCallbacks).getKoinScope()

        // THEN
        Assert.assertEquals(input.scope, result)
    }

    @Test
    fun `GIVEN KoinScopeComponent WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = koinScopeComponent

        // WHEN
        val result = (input as ComponentCallbacks).getKoinScope()

        // THEN
        Assert.assertEquals(input.scope, result)
    }

    @Test
    fun `GIVEN KoinComponent WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = koinComponent

        // WHEN
        val result = (input as ComponentCallbacks).getKoinScope()

        // THEN
        Assert.assertEquals(input.getKoin().scopeRegistry.rootScope, result)
    }

    @Test
    fun `GIVEN ComponentCallbacks WHEN get koin scope THEN retrieve expected scope`() {
        // GIVEN
        val input = componentCallbacks

        // WHEN
        val result = input.getKoinScope()

        // THEN
        Assert.assertEquals(GlobalContext.get().scopeRegistry.rootScope, result)
    }

}
