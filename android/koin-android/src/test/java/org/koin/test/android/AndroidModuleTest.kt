package org.koin.test.android

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.log.PrintLogger
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

    val SampleModule = applicationContext {
        provide { mock(Application::class.java) }
        provide { AndroidComponent(get()) }
    }


    val BadModule = applicationContext {
        provide { AndroidComponent(get()) }
    }

    val ActivityModule = applicationContext {
        context(CTX_ACTIVITY_MODULE) {
            provide { OtherService(get(), getProperty(URL)) }
        }

    }
    val CTX_ACTIVITY_MODULE = "ActivityModule"

    class AndroidComponent(val application: Application)

    class OtherService(val androidComponent: AndroidComponent, val url: String)

    @Before
    fun before(){
        Koin.logger = PrintLogger()
    }

    @Test
    fun should_inject_by_scope() {
        startKoin(listOf(SampleModule, ActivityModule))

        assertContexts(2)
        assertDefinitions(3)
        assertRemainingInstances(0)
        assertContextInstances(CTX_ACTIVITY_MODULE, 0)

        setProperty(URL, "URL")

        val service = get<OtherService>()
        val component = get<AndroidComponent>()

        assertEquals(component, service.androidComponent)
        assertEquals(get<Application>(), component.application)

        assertContextInstances(CTX_ACTIVITY_MODULE, 1)
        assertDefinitions(3)
        assertRemainingInstances(3)

        releaseContext(CTX_ACTIVITY_MODULE)
        assertContextInstances(CTX_ACTIVITY_MODULE, 0)
        assertDefinitions(3)
        assertRemainingInstances(2)
    }

    @Test
    fun should_scope_no_scope() {
        startKoin(listOf(BadModule))

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
        startKoin(listOf(SampleModule))

        assertDefinitions(2)
        assertRemainingInstances(0)

        val component = get<AndroidComponent>()

        assertEquals(get<Application>(), component.application)

        assertDefinitions(2)
        assertRemainingInstances(2)
    }

    @Test
    fun should_not_run() {
        startKoin(listOf(BadModule))

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