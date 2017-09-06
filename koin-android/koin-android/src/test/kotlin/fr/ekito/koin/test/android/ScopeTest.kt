package fr.ekito.koin.test.android

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import fr.ekito.koin.test.ext.assertRootScopeSize
import fr.ekito.koin.test.ext.assertScopeSize
import fr.ekito.koin.test.ext.assertScopes
import fr.ekito.koin.test.ext.assertSizes
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Koin
import org.koin.android.init
import org.mockito.ArgumentMatchers
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

        ctx.assertScopes(2)
        ctx.assertSizes(3, 0)
        ctx.assertScopeSize(Activity::class, 0)

        ctx.setProperty("url",urlValue)

        val service_1 = ctx.get<OtherService>()
        val component = ctx.get<AndroidComponent>()

        assertEquals(component, service_1.androidComponent)
        assertEquals(applicationContext, component.application)
        ctx.assertScopeSize(Activity::class, 1)
        ctx.assertSizes(3, 3)

        ctx.release(Activity::class)
        ctx.assertScopeSize(Activity::class, 0)
        ctx.assertSizes(3, 2)

        val service_2 = ctx.get<OtherService>()
        assertEquals(component, service_2.androidComponent)
        assertEquals(applicationContext, component.application)
        ctx.assertScopeSize(Activity::class, 1)
        ctx.assertSizes(3, 3)
    }

    @Test
    fun should_not_inject_by_scope() {
        val ctx = Koin().build(SampleModule())

        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)

        Assert.assertNull(ctx.getOrNull<AndroidComponent>())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        ctx.assertRootScopeSize(0)
    }
}