package org.koin.test.android

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.koin.Koin
import org.koin.android.AndroidModule
import org.koin.android.init
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances
import org.koin.test.ext.getOrNull
import org.mockito.Mockito.mock

/**
 * Android & Koin Context Tests
 */
class ContextTest {

    class SimpleModule : AndroidModule() {
        override fun context() = applicationContext {
            provide { AndroidComponent(get()) }
        }
    }

    @Test
    fun should_initialize_context() {
        val applicationContext = mock(Application::class.java)

        val ctx = Koin().init(applicationContext).build(emptyList())

        ctx.assertDefinitions(1)
        ctx.assertRemainingInstances(0)

        val found_appContext = ctx.get<Application>()

        assertEquals(applicationContext, found_appContext)

        ctx.assertRemainingInstances(1)
    }

    @Test
    fun should_not_initialize_context() {
        val ctx = Koin().build(emptyList())

        ctx.assertDefinitions(0)
        ctx.assertRemainingInstances(0)

        assertNull(ctx.getOrNull<Application>())
    }

    @Test
    fun should_init_context_and_dependency() {
        val applicationContext = mock(Application::class.java)

        val ctx = Koin().init(applicationContext).build(SimpleModule())

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)

        val component = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, component.application)

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(2)
    }
}