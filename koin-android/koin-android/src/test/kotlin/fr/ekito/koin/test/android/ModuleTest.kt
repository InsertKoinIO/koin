package fr.ekito.koin.test.android

import android.app.Application
import android.content.res.Resources
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.android.init
import org.koin.error.BeanInstanceCreationException
import org.koin.test.ext.assertProps
import org.koin.test.ext.assertSizes
import org.koin.test.ext.getOrNull
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class ModuleTest {

    @Test
    fun should_initialize_module() {
        val applicationContext = mock(Application::class.java)
        val ctx = Koin().init(applicationContext).build(SampleModule())

        ctx.assertSizes(2, 0)

        val found_appContext = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, found_appContext.application)

        ctx.assertSizes(2, 2)
    }

    @Test
    fun should_initialize_complex_module() {
        val applicationContext = mock(Application::class.java)
        val resources = mock(Resources::class.java)
        val urlValue = "url value"
        `when`(applicationContext.resources).thenReturn(resources)

        val ctx = Koin().init(applicationContext).build(ComplexModule())
        ctx.setProperty("url", urlValue)

        ctx.assertSizes(3, 0)

        val found_appContext = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, found_appContext.application)

        ctx.assertSizes(3, 2)
        ctx.assertProps(1)
    }

    @Test
    fun should_not_initialize_complex_module() {
        val ctx = Koin().build(ComplexModule())

        ctx.assertSizes(2, 0)

        Assert.assertNull(ctx.getOrNull<OtherService>())

        ctx.assertSizes(2, 0)
    }

    @Test
    fun should_not_initialize_complex_module_exception() {
        val ctx = Koin().build(ComplexModule())

        ctx.assertSizes(2, 0)

        try {
            val service = ctx.get<OtherService>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            assertNotNull(e)
        }
        ctx.assertSizes(2, 0)
    }

    @Test
    fun should_initialize_module_properties() {
        val applicationContext = mock(Application::class.java)
        val urlValue = "url value"
        val ctx = Koin().properties(mapOf(OtherService.URL to urlValue)).init(applicationContext).build(ComplexPropertyModule())

        ctx.assertSizes(3, 0)
        ctx.assertProps(1)
        val found_appContext = ctx.get<OtherService>()

        assertEquals(applicationContext, found_appContext.androidComponent.application)
        assertEquals(urlValue, found_appContext.url)

        ctx.assertSizes(3, 3)
        ctx.assertProps(1)
    }

    @Test
    fun should_not_initialize_module_properties() {
        val applicationContext = mock(Application::class.java)
        val ctx = Koin().init(applicationContext).build(ComplexPropertyModule())

        ctx.assertSizes(3, 0)
        ctx.assertProps(0)

        try {
            val found_appContext = ctx.get<OtherService>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            assertNotNull(e)
        }

        ctx.assertSizes(3, 2)
        ctx.assertProps(0)
    }
}