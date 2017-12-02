package org.koin.test.android

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.android.module.AndroidModule
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.releaseContext
import org.koin.standalone.setProperty
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContextInstances
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class AndroidModuleTest : AbstractKoinTest() {

    companion object {
        val URL = "URL"
    }

    class SampleModule : AndroidModule() {
        override fun context() =
                applicationContext {
                    provide { mock(Application::class.java) }
                    provide { AndroidComponent(androidApplication) }
                }
    }

    class BadModule : AndroidModule() {
        override fun context() =
                applicationContext {
                    provide { AndroidComponent(androidApplication) }
                }
    }

    class ActivityModule : AndroidModule() {
        override fun context() =
                applicationContext {
                    context(CTX_ACTIVITY_MODULE) {
                        provide { OtherService(get(), getProperty(URL)) }
                    }
                }

        companion object {
            val CTX_ACTIVITY_MODULE = "ActivityModule"
        }
    }

    class AndroidComponent(val application: Application)

    class OtherService(val androidComponent: AndroidComponent, val url: String)

    @Test
    fun should_inject_by_scope() {
        startKoin(listOf(SampleModule(), ActivityModule()))

        assertContexts(2)
        assertDefinitions(3)
        assertRemainingInstances(0)
        assertContextInstances(ActivityModule.CTX_ACTIVITY_MODULE, 0)

        setProperty(URL, "URL")

        val service = get<OtherService>()
        val component = get<AndroidComponent>()

        assertEquals(component, service.androidComponent)
        assertEquals(get<Application>(), component.application)

        assertContextInstances(ActivityModule.CTX_ACTIVITY_MODULE, 1)
        assertDefinitions(3)
        assertRemainingInstances(3)

        releaseContext(ActivityModule.CTX_ACTIVITY_MODULE)
        assertContextInstances(ActivityModule.CTX_ACTIVITY_MODULE, 0)
        assertDefinitions(3)
        assertRemainingInstances(2)
    }

    @Test
    fun should_scope_no_scope() {
        startKoin(listOf(BadModule()))

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<AndroidComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstances(0)
    }

    @Test
    fun should_init_context_and_dependency() {
        startKoin(listOf(SampleModule()))

        assertDefinitions(2)
        assertRemainingInstances(0)

        val component = get<AndroidComponent>()

        assertEquals(get<Application>(), component.application)

        assertDefinitions(2)
        assertRemainingInstances(2)
    }

    @Test
    fun should_not_run() {
        startKoin(listOf(BadModule()))

        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<AndroidComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertDefinitions(1)
        assertRemainingInstances(0)
    }
}