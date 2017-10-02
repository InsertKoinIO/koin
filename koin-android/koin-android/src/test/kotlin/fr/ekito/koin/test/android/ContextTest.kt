package fr.ekito.koin.test.android

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.koin.Koin
import org.koin.android.init
import fr.ekito.koin.test.ext.assertSizes
import fr.ekito.koin.test.ext.getOrNull
import org.mockito.Mockito.mock

/**
 * Android & Koin Context Tests
 */
class ContextTest {

    @Test
    fun should_initialize_context() {
        val applicationContext = mock(Application::class.java)

        val ctx = Koin().init(applicationContext).build()

        ctx.assertSizes(1, 0)

        val found_appContext = ctx.get<Application>()

        assertEquals(applicationContext, found_appContext)

        ctx.assertSizes(1, 1)
    }

    @Test
    fun should_not_initialize_context() {
        val ctx = Koin().build()

        ctx.assertSizes(0, 0)

        assertNull(ctx.getOrNull<Application>())
    }

    @Test
    fun should_init_context_and_dependency() {
        val applicationContext = mock(Application::class.java)

        val ctx = Koin().init(applicationContext).build()
        ctx.provide { AndroidComponent(ctx.get()) }

        ctx.assertSizes(2, 0)

        val component = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, component.application)

        ctx.assertSizes(2, 2)
    }
}