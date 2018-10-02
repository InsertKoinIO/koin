package org.koin.test.android

import android.app.Application
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.with
import org.koin.core.Koin
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.setProperty
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContextInstances
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class AndroidModuleTest : AutoCloseKoinTest() {

    companion object {
        val URL = "URL"
    }

    val SampleModule = module {
        single { AndroidComponent(androidContext()) }
    }

    val ActivityModule = module {
        module(CTX_ACTIVITY_MODULE) {
            single { OtherService(get(), getProperty(URL)) }
        }

    }
    val CTX_ACTIVITY_MODULE = "ActivityModule"

    class AndroidComponent(val androidContext: Context)
    class OtherService(val androidComponent: AndroidComponent, val url: String)

    @Before
    fun before() {
        Koin.logger = PrintLogger()
    }

    @Test
    fun should_inject_by_scope() {
        startKoin(listOf(SampleModule, ActivityModule)) with (mock(Context::class.java))

        assertContexts(2)
        assertDefinitions(3)
        assertRemainingInstanceHolders(0)
        assertContextInstances(CTX_ACTIVITY_MODULE, 0)

        setProperty(URL, "URL")

        val service = get<OtherService>()
        val component = get<AndroidComponent>()

        assertEquals(component, service.androidComponent)
        assertEquals(get<Context>(), component.androidContext)

        assertContextInstances(CTX_ACTIVITY_MODULE, 1)
        assertDefinitions(3)
        assertRemainingInstanceHolders(3)

//        release(CTX_ACTIVITY_MODULE)
//        assertContextInstances(CTX_ACTIVITY_MODULE, 1)
//        assertDefinitions(3)
//        assertRemainingInstanceHolders(3)
    }

    @Test
    fun should_scope_no_scope() {
        startKoin(listOf(SampleModule))

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstanceHolders(0)

        try {
            get<AndroidComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }

    @Test
    fun should_init_context_and_dependency() {
        startKoin(listOf(SampleModule)) with (mock(Application::class.java))

        assertDefinitions(3)
        assertRemainingInstanceHolders(0)

        val component = get<AndroidComponent>()

        assertEquals(get<Context>(), component.androidContext)

        assertRemainingInstanceHolders(2)
    }

    @Test
    fun should_not_run() {
        startKoin(listOf(SampleModule))

        assertDefinitions(1)
        assertRemainingInstanceHolders(0)

        try {
            get<AndroidComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }
}