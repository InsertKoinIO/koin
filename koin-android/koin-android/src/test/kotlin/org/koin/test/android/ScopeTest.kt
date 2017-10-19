package org.koin.test.android

import android.app.Application
import android.content.res.Resources
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Koin
import org.koin.android.ext.koin.init
import org.koin.test.ext.*
import org.mockito.Mockito
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class ScopeTest {

    @Test
    fun should_inject_by_scope() {
        val applicationContext = mock(Application::class.java)
        val resources = mock(Resources::class.java)
        val urlValue = "url value"
        Mockito.`when`(applicationContext.resources).thenReturn(resources)

        val ctx = Koin().init(applicationContext).build(SampleModule(), ActivityModule())

        ctx.assertContexts(2)
        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(0)
        ctx.assertScopeInstances(ActivityModule.CTX_ACTIVITY_MODULE, 0)

        ctx.setProperty("url", urlValue)

        val service_1 = ctx.get<OtherService>()
        val component = ctx.get<AndroidComponent>()

        assertEquals(component, service_1.androidComponent)
        assertEquals(applicationContext, component.application)
        ctx.assertScopeInstances(ActivityModule.CTX_ACTIVITY_MODULE, 1)
        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(3)

        ctx.release(ActivityModule.CTX_ACTIVITY_MODULE)
        ctx.assertScopeInstances(ActivityModule.CTX_ACTIVITY_MODULE, 0)
        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(2)
    }

    @Test
    fun should_not_inject_by_scope() {
        val ctx = Koin().build(SampleModule())

        ctx.assertContexts(1)
        ctx.assertDefinitions(1)
        ctx.assertRemainingInstances(0)

        Assert.assertNull(ctx.getOrNull<AndroidComponent>())
        ctx.assertContexts(1)
        ctx.assertDefinitions(1)
        ctx.assertRemainingInstances(0)
    }
}