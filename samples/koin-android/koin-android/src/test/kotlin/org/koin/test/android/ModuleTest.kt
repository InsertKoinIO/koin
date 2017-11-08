package org.koin.test.android

import android.app.Application
import android.content.res.Resources
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.android.ext.koin.init
import org.koin.error.BeanInstanceCreationException
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertProperties
import org.koin.test.ext.assertRemainingInstances
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

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)

        val found_appContext = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, found_appContext.application)

        ctx.assertRemainingInstances(2)
    }

    @Test
    fun should_initialize_complex_module() {
        val applicationContext = mock(Application::class.java)
        val resources = mock(Resources::class.java)
        val urlValue = "url value"
        `when`(applicationContext.resources).thenReturn(resources)

        val ctx = Koin().init(applicationContext).build(ComplexModule())
        ctx.setProperty("url", urlValue)

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(0)

        val found_appContext = ctx.get<AndroidComponent>()

        assertEquals(applicationContext, found_appContext.application)

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(2)
        ctx.assertProperties(1)
    }

    @Test
    fun should_not_initialize_complex_module() {
        val ctx = Koin().build(ComplexModule())

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)

        Assert.assertNull(ctx.getOrNull<OtherService>())

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)
    }

    @Test
    fun should_not_initialize_complex_module_exception() {
        val ctx = Koin().build(ComplexModule())

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)

        try {
            val service = ctx.get<OtherService>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            assertNotNull(e)
        }
        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)
    }

    @Test
    fun should_initialize_module_properties() {
        val applicationContext = mock(Application::class.java)
        val urlValue = "url value"
        val ctx = Koin().properties(mapOf(OtherService.URL to urlValue)).init(applicationContext).build(ComplexPropertyModule())

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(0)
        ctx.assertProperties(1)
        val found_appContext = ctx.get<OtherService>()

        assertEquals(applicationContext, found_appContext.androidComponent.application)
        assertEquals(urlValue, found_appContext.url)

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(3)
    }

    @Test
    fun should_not_initialize_module_properties() {
        val applicationContext = mock(Application::class.java)
        val ctx = Koin().init(applicationContext).build(ComplexPropertyModule())

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(0)
        ctx.assertProperties(0)

        try {
            val found_appContext = ctx.get<OtherService>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            assertNotNull(e)
        }

        ctx.assertDefinitions(3)
        ctx.assertRemainingInstances(2)
    }
}